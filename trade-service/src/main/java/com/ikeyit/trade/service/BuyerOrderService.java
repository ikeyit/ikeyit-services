package com.ikeyit.trade.service;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.common.utils.IpUtils;
import com.ikeyit.mqhelper.MqSender;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.pay.dto.CreatePayOrderParam;
import com.ikeyit.pay.dto.SubmitPayOrderParam;
import com.ikeyit.pay.service.PayService;
import com.ikeyit.trade.domain.Order;
import com.ikeyit.trade.domain.OrderItem;
import com.ikeyit.trade.domain.OrderLog;
import com.ikeyit.trade.domain.Refund;
import com.ikeyit.trade.dto.*;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.feign.AddressClient;
import com.ikeyit.trade.feign.AddressDTO;
import com.ikeyit.trade.feign.ProductClient;
import com.ikeyit.trade.feign.ReduceStockParam;
import com.ikeyit.trade.mq.OrderMessage;
import com.ikeyit.trade.repository.OrderItemRepository;
import com.ikeyit.trade.repository.OrderLogRepository;
import com.ikeyit.trade.repository.OrderRepository;
import com.ikeyit.trade.repository.RefundRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Service
public class BuyerOrderService {

    private static Logger log = LoggerFactory.getLogger(BuyerOrderService.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderLogRepository orderLogRepository;

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    CartService cartService;

    @Autowired
    SellerRefundService refundService;

    @Autowired
    AddressClient addressClient;

    @Autowired
    ProductClient productClient;

    @Autowired
    MqSender mqSender;

    @Value("${order.timeout.seconds:600}")
    int orderTimeoutSeconds;

    String paymentOrderIdPrefix = "test_";


    @Autowired
    PayService payService;

    /**
     * 通过购物车购买后的订单确认
     * @param cartItemIds
     * @return
     */
    public OrderConfirmationDTO createOrderConfirmation(Long[] cartItemIds) {
        List<CartItemDTO> cartItems = getCartItems(cartItemIds);
        return createOrderConfirmation(cartItems);
    }

    private List<CartItemDTO> getCartItems(Long[] cartItemIds) {
        List<CartItemDTO> cartItems = new ArrayList<CartItemDTO>(cartItemIds.length);
        for(Long cartItemId : cartItemIds) {
            CartItemDTO cartItem = cartService.getCartItem(cartItemId);
            cartItems.add(cartItem);
        }

        return cartItems;
    }

    /**
     * 立即购买后的订单确认
     * @param skuId
     * @param quantity
     * @return
     */
    public OrderConfirmationDTO createOrderConfirmation(Long skuId, Integer quantity) {
        CartItemDTO cartItem = cartService.createTempCartItem(skuId, quantity);
        return createOrderConfirmation(Collections.singletonList(cartItem));
    }


    private OrderConfirmationDTO createOrderConfirmation(List<CartItemDTO> cartItems) {
        OrderConfirmationDTO orderConfirmation = new OrderConfirmationDTO();
        AddressDTO address = addressClient.getUserDefaultAddress();
        orderConfirmation.setAddress(address);
        List<OrderItemDTO> orderItems = new ArrayList<>(cartItems.size());
        for(CartItemDTO cartItem : cartItems) {
            OrderItemDTO orderItem = new OrderItemDTO();
            orderItem.setSkuId(cartItem.getSkuId());
            orderItem.setImage(cartItem.getImage());
            orderItem.setTitle(cartItem.getTitle());
            orderItem.setSkuAttributes(cartItem.getSkuName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItems.add(orderItem);
        }

        orderConfirmation.setItems(orderItems);

        BigDecimal total = BigDecimal.ZERO;
        int totalQuantity = 0;
        for (OrderItemDTO item : orderItems) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalQuantity += item.getQuantity();
        }
        //TODO 邮费
        orderConfirmation.setFreightAmount(BigDecimal.ZERO);
        //TODO 计算折扣金额
        orderConfirmation.setDiscountAmount(BigDecimal.ZERO);

        BigDecimal paymentAmount = total.subtract(orderConfirmation.getDiscountAmount());

        orderConfirmation.setGoodsAmount(total);
        orderConfirmation.setPaymentAmount(paymentAmount);
        orderConfirmation.setGoodsQuantity(totalQuantity);

        //TODO 防重复提交
        //TODO 优惠信息，可选支付方法

        return orderConfirmation;
    }

    /**
     * 创建订单
     * @param orderParam
     * @return
     */
    @Transactional
    public Long createOrder(OrderParam orderParam) {
        if (orderParam == null || orderParam.getAddressId() == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        boolean fromCart = true;
        Long[] cartItemIds = orderParam.getCartItemIds();
        List<CartItemDTO> cartItems = null;
        if (cartItemIds != null) {
            //通过购物车来创建订单
            if (cartItemIds.length == 0)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
            cartItems = getCartItems(cartItemIds);
        } else {
            //立即购买
            Long skuId = orderParam.getSkuId();
            Integer quantity = orderParam.getQuantity();
            if (skuId == null || quantity == null || quantity <= 0)
                throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
            fromCart = false;
            cartItems = Arrays.asList(cartService.createTempCartItem(skuId, quantity));
        }

        //创建订单
        Order order = buildOrder(orderParam);
        //创建订单项
        List<OrderItem> orderItems = buildOrderItems(order, cartItems);
        //计算优惠/总金额/分摊金额
        calculateMoney(order, orderItems);
        //存入数据库
        orderRepository.create(order);
        Long orderId = order.getId();
        for (OrderItem item : orderItems) {
            item.setOrderId(orderId);
            item.setStatus(order.getStatus());
            orderItemRepository.create(item);
        }

        //创建支付订单
        CreatePayOrderParam createPayOrderParam = new CreatePayOrderParam();
        createPayOrderParam.setBuyerId(order.getBuyerId());
        createPayOrderParam.setPayWay(order.getPayWay());
        createPayOrderParam.setOrderId(orderId);
        createPayOrderParam.setOrderType(Constants.PAY_ORDER_TYPE);
        createPayOrderParam.setExpireTime(order.getExpireTime());
        createPayOrderParam.setPaymentAmount(order.getPaymentAmount());
        createPayOrderParam.setSubject(buildPaySubject(orderItems));
        payService.createPayOrder(createPayOrderParam);

        try {
            //远程扣减库存
            ReduceStockParam reduceStockParam = new ReduceStockParam();
            reduceStockParam.setOrderId(orderId);
            List<ReduceStockParam.ReduceStockItem> reduceStockItems = orderItems.stream().map(orderItem -> {
                ReduceStockParam.ReduceStockItem reduceStockItem = new ReduceStockParam.ReduceStockItem();
                reduceStockItem.setSkuId(orderItem.getSkuId());
                reduceStockItem.setQuantity(orderItem.getQuantity());
                return reduceStockItem;
            }).collect(Collectors.toList());

            reduceStockParam.setItems(reduceStockItems);
            productClient.reduceStock(reduceStockParam);

            //TODO 远程锁定优惠券

            //TODO 占用积分

            if (fromCart)
                cartService.deleteCartItems(orderParam.getCartItemIds());

            //发送消息到MQ
            OrderMessage orderMessage = new OrderMessage();
            orderMessage.setOrderId(orderId);
            orderMessage.setQuantity(order.getTotalQuantity());
            orderMessage.setBuyerId(order.getBuyerId());
            orderMessage.setSellerId(order.getSellerId());
            orderMessage.setOrderStatus(order.getStatus());
            orderMessage.setTotalAmount(order.getGoodsAmount());

            OrderItem orderItem = orderItems.get(0);
            orderMessage.setTitle(orderItem.getTitle());
            orderMessage.setImage(orderItem.getImage());
            mqSender.asyncSendAfterCommit("trade_order:created", orderMessage, Constants.MQ_KEY_PREFIX_ORDER + orderId);
            //发送延时消息到MQ，用于订单超时取消
            mqSender.asyncSendAfterCommit("trade_order:timeout", orderId, Constants.MQ_KEY_PREFIX_ORDER + orderId, order.getExpireTime());
        } catch (Exception e) {
            log.error("创建订单失败", e);

            //出现异常，事务回滚
            mqSender.asyncSend("trade_order:cancelled", orderId, Constants.MQ_KEY_PREFIX_ORDER + orderId);
            //继续抛出异常回滚事务
            throw e;
        }

        return order.getId();
    }

    private String buildPaySubject(List<OrderItem> orderItems) {
        String description = "无";
        if (orderItems != null && !orderItems.isEmpty()) {
            OrderItem firstOrderItem = orderItems.get(0);
            description = firstOrderItem.getTitle() + "[" + firstOrderItem.getSkuAttributes() + "]";
            if (orderItems.size() > 1)
                description += "等多件商品";
        }
        return description;
    }

    private OrderDTO buildOrderDTO(Order order, List<OrderItem> orderItems, boolean withRefundInfo) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());

        List<OrderItemDTO> itemDTOs = new ArrayList<>(orderItems.size());
        for(OrderItem orderItem : orderItems) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(orderItem.getId());
            itemDTO.setSkuId(orderItem.getSkuId());
            itemDTO.setImage(orderItem.getImage());
            itemDTO.setTitle(orderItem.getTitle());
            itemDTO.setSkuAttributes(orderItem.getSkuAttributes());
            itemDTO.setPrice(orderItem.getPrice());
            itemDTO.setPaymentAmount(orderItem.getPaymentAmount());
            itemDTO.setQuantity(orderItem.getQuantity());
            if (withRefundInfo) {
                Refund refund = refundRepository.getStatusIsNot(orderItem.getId(), Refund.STATUS_CLOSED);
                if (refund != null) {
                    itemDTO.setRefundId(refund.getId());
                    itemDTO.setRefundStatus(refund.getStatus());
                }
            }
            itemDTOs.add(itemDTO);
        }

        orderDTO.setItems(itemDTOs);
        orderDTO.setReceiverName(order.getReceiverName());
        orderDTO.setReceiverPhone(order.getReceiverPhone());
        orderDTO.setReceiverProvince(order.getReceiverProvince());
        orderDTO.setReceiverCity(order.getReceiverCity());
        orderDTO.setReceiverDistrict(order.getReceiverDistrict());
        orderDTO.setReceiverStreet(order.getReceiverStreet());
        orderDTO.setFreightAmount(order.getFreightAmount());
        orderDTO.setDiscountAmount(order.getDiscountAmount());
        orderDTO.setGoodsAmount(order.getGoodsAmount());
        orderDTO.setPaymentAmount(order.getPaymentAmount());
        orderDTO.setGoodsQuantity(order.getTotalQuantity());
        orderDTO.setLogisticsCompany(order.getLogisticsCompany());
        orderDTO.setTrackingNumber(order.getTrackingNumber());
        orderDTO.setShipTime(order.getShipTime());
        orderDTO.setPayTime(order.getPayTime());
        orderDTO.setFinishTime(order.getFinishTime());
        orderDTO.setCreateTime(order.getCreateTime());
        return orderDTO;
    }




    /**
     * 买家取消订单
     * @param orderId
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderSafely(orderId);
        Integer status = order.getStatus();
        if (!status.equals(Order.STATUS_WAIT_BUYER_PAY))
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());

        order.setFinishTime(LocalDateTime.now());
        order.setStatus(Order.STATUS_CLOSED);
        order.setCloseReason(Order.CLOSE_REASON_BUYER_CANCELLED);
        if (orderRepository.updateStatus(order, status) != 1)
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());

        orderItemRepository.updateStatusByOrderId(orderId, Order.STATUS_CLOSED);
        saveOrderLog(order, "取消订单", "用户主动取消订单");
        mqSender.asyncSendAfterCommit("trade_order:cancelled", orderId, Constants.MQ_KEY_PREFIX_ORDER + orderId);
    }

    /**
     * 用户确认订单
     *
     * @param orderId
     */
    @Transactional
    public void finishOrder(Long orderId) {
        Order order = getOrderSafely(orderId);
        Integer status = order.getStatus();
        if (!status.equals(Order.STATUS_SHIPPED))
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());

        order.setFinishTime(LocalDateTime.now());
        order.setStatus(Order.STATUS_FINISHED);
        if (orderRepository.updateStatus(order, status) != 1)
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());

        List<OrderItem> orderItems = orderItemRepository.getByOrderId(orderId);
        orderItems.forEach(orderItem -> {
            Integer itemStatus = orderItem.getStatus();
            if (Order.STATUS_SHIPPED.equals(itemStatus)) {
                orderItem.setStatus(Order.STATUS_FINISHED);
                if (orderItemRepository.update(orderItem, itemStatus)!=1)
                    throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());
            }

        });


        saveOrderLog(order, "确认收货", "用户确认收货");
    }


    /**
     * 返回的订单详细信息
     * @param orderId
     * @return
     */
    public OrderDTO getOrder(Long orderId) {
        Order order = getOrderSafely(orderId);
        List<OrderItem> orderItems = orderItemRepository.getByOrderId(orderId);
        return buildOrderDTO(order, orderItems, true);
    }

    /**
     * 返回当前用户的订单列表
     * @param status
     * @param pageParam
     * @return
     */
    public Page<OrderDTO> getOrders(Integer status, PageParam pageParam) {
        Long userId = authenticationService.getCurrentUserId();
        Page<Order> orders = orderRepository.get(null, userId, null, status, null, null, null,
                null, null, null,  pageParam);
        List<OrderDTO> orderDTOs = orders.getContent().stream().map(order -> {
            List<OrderItem> orderItems = orderItemRepository.getByOrderId(order.getId());
            return buildOrderDTO(order, orderItems, false);
        }).collect(Collectors.toList());

        Page<OrderDTO> page = new Page<>(orderDTOs, pageParam, orders.getTotal());
        return page;

    }

    private Order getOrderSafely(Long orderId) {
        if (orderId == null)
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, "null");
        Order order = orderRepository.getById(orderId);
        if (order == null)
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, orderId.toString());

        Long userId = authenticationService.getCurrentUserId();
        if (!order.getBuyerId().equals(userId))
            throw new BusinessException(TradeErrorCode.ORDER_ILLEGAL_ACCESS, orderId.toString());

        return order;
    }


    private Order buildOrder(OrderParam orderParam) {
        Long userId = authenticationService.getCurrentUserId();
        Order order = new Order();
        order.setStatus(Order.STATUS_WAIT_BUYER_PAY);
        order.setCloseReason(0);
        order.setBuyerId(userId);
        order.setBuyerName("未实现" + userId.toString());
        order.setBuyerMemo(orderParam.getBuyerMemo());
        order.setPayWay(orderParam.getPayWay());
        order.setExpireTime(LocalDateTime.now().plusSeconds(orderTimeoutSeconds));
        order.setSource(orderParam.getSource());
        order.setOrderType(0);

        //设置收货地址
        AddressDTO address = addressClient.getUserAddress(orderParam.getAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhone());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverDistrict(address.getDistrict());
        order.setReceiverStreet(address.getStreet());

        //TODO 设置发票信息
        return order;
    }


    private List<OrderItem> buildOrderItems(Order order, List<CartItemDTO> cartItems) {
        //创建OrderItem
        List<OrderItem> orderItems = new ArrayList<>(cartItems.size());
        for(CartItemDTO cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setStatus(order.getStatus());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setSellerId(cartItem.getSellerId());
            //设置订单的卖家
            order.setSellerId(cartItem.getSellerId());

            orderItem.setBuyerId(order.getBuyerId());
            orderItem.setSkuId(cartItem.getSkuId());
            orderItem.setSkuAttributes(cartItem.getSkuName());
//            orderItem.setSkuCode();

            orderItem.setSkuId(cartItem.getSkuId());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setTitle(cartItem.getTitle());
            orderItem.setImage(cartItem.getImage());
            orderItems.add(orderItem);
        }

        return orderItems;
    }


    private void calculateMoney(Order order, List<OrderItem> orderItems ) {
        BigDecimal total = BigDecimal.ZERO;
        Long totalQuantity = 0L;
        for (OrderItem item : orderItems) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalQuantity += item.getQuantity();
        }
        //TODO 邮费
        order.setFreightAmount(BigDecimal.ZERO);
        //TODO 计算折扣金额
        order.setDiscountAmount(BigDecimal.ZERO);

        order.setTotalQuantity(totalQuantity);
        order.setGoodsAmount(total);

        BigDecimal paymentAmount = order.getGoodsAmount()
                .subtract(order.getDiscountAmount());
        if (paymentAmount.compareTo(BigDecimal.ZERO) < 0)
            paymentAmount = BigDecimal.ZERO;

        order.setPaymentAmount(paymentAmount);

        //分担
        BigDecimal ratio = paymentAmount.divide(total);
        for (OrderItem item : orderItems) {
            item.setPaymentAmount(ratio.multiply(item.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));
        }
    }

    public void payOrderCheck(Long orderId, String payWay) {
        payService.syncPayResult(Constants.PAY_ORDER_TYPE, orderId, payWay);
    }

    /**
     * 调用支付商预下单
     * @param orderId
     * @return
     */
    public Object submitPayOrder(Long orderId, String payWay, Map<String, Object> params) {
        SubmitPayOrderParam submitPayOrderParam = new SubmitPayOrderParam();
        submitPayOrderParam.setOrderType(Constants.PAY_ORDER_TYPE);
        submitPayOrderParam.setOrderId(orderId);
        submitPayOrderParam.setPayWay(payWay);
        submitPayOrderParam.setClientIp(IpUtils.getClientIp());
        submitPayOrderParam.setParams(params);
        return payService.submitPayOrder(submitPayOrderParam);
    }

    public String getPaymentOrderIdPrefix() {
        return paymentOrderIdPrefix;
    }

    public void setPaymentOrderIdPrefix(String paymentOrderIdPrefix) {
        this.paymentOrderIdPrefix = paymentOrderIdPrefix;
    }

    private void saveOrderLog(Order order, String operation, String message) {
        //记录订单流水
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(order.getId());
        orderLog.setOrderStatus(order.getStatus());
        orderLog.setOperator("买家：" + authenticationService.getCurrentUserId());
        orderLog.setOperation(operation);
        orderLog.setMessage(message);
        orderLogRepository.create(orderLog);
    }

    public String toPaymentOrderId(Long orderId) {
        return paymentOrderIdPrefix + orderId;
    }

    public Long fromPaymentOrderId(String paymentOrderId) {
        if (paymentOrderId.startsWith(paymentOrderIdPrefix))
            return new Long(paymentOrderId.substring(paymentOrderIdPrefix.length()));
        throw new IllegalArgumentException("商户订单号格式不正确");
    }
}
