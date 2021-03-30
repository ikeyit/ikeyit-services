package com.ikeyit.trade.service;


import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.trade.domain.Order;
import com.ikeyit.trade.domain.OrderItem;
import com.ikeyit.trade.domain.OrderLog;
import com.ikeyit.trade.dto.OrderDTO;
import com.ikeyit.trade.dto.OrderItemDTO;
import com.ikeyit.trade.dto.SellerGetOrdersParam;
import com.ikeyit.trade.dto.ShipOrderParam;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.feign.ProductClient;
import com.ikeyit.trade.feign.UserClient;
import com.ikeyit.trade.feign.UserDTO;
import com.ikeyit.trade.repository.OrderItemRepository;
import com.ikeyit.trade.repository.OrderLogRepository;
import com.ikeyit.trade.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 卖家订单服务
 */
@Service
public class SellerOrderService {


    private static Logger log = LoggerFactory.getLogger(SellerOrderService.class);

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderLogRepository orderLogRepository;

    @Autowired
    ProductClient productClient;

    @Autowired
    UserClient userClient;

    /**
     * 返回的订单详细信息
     * @param orderId
     * @return
     */
    public OrderDTO getOrder(Long orderId) {
        Order order = getOrderSafely(orderId);
        List<OrderItem> orderItems = orderItemRepository.getByOrderId(orderId);
        return buildOrderDTO(order, orderItems);
    }

    public Page<OrderDTO> getOrders(SellerGetOrdersParam sellerGetOrdersParam, String sort, PageParam pageParam) {
        Long sellerId = authenticationService.getCurrentUserId();
        Page<Order> orders = orderRepository.get(
                sellerGetOrdersParam.getOrderId(),
                sellerGetOrdersParam.getBuyerId(),
                sellerId,
                sellerGetOrdersParam.getStatus(),
                sellerGetOrdersParam.getReceiverName(),
                sellerGetOrdersParam.getReceiverPhone(),
                sellerGetOrdersParam.getTrackingNumber(),
                sellerGetOrdersParam.getCreateTimeStart(),
                sellerGetOrdersParam.getCreateTimeEnd(),
                sort,
                pageParam);

        return Page.map(orders, order -> {
                List<OrderItem> orderItems = orderItemRepository.getByOrderId(order.getId());
                return buildOrderDTO(order, orderItems);
        });

    }


    private OrderDTO buildOrderDTO(Order order, List<OrderItem> orderItems) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setStatus(order.getStatus());

        List<OrderItemDTO> itemDTOs = new ArrayList<>(orderItems.size());
        for(OrderItem orderItem : orderItems) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(orderItem.getId());
            itemDTO.setStatus(orderItem.getStatus());
            itemDTO.setProductId(orderItem.getProductId());
            itemDTO.setSkuCode(orderItem.getSkuCode());
            itemDTO.setSkuId(orderItem.getSkuId());
            itemDTO.setImage(orderItem.getImage());
            itemDTO.setTitle(orderItem.getTitle());
            itemDTO.setSkuAttributes(orderItem.getSkuAttributes());
            itemDTO.setPrice(orderItem.getPrice());
            itemDTO.setQuantity(orderItem.getQuantity());
            itemDTO.setPaymentAmount(orderItem.getPaymentAmount());
            itemDTOs.add(itemDTO);
        }

        orderDTO.setItems(itemDTOs);
        orderDTO.setBuyerId(order.getBuyerId());


        UserDTO user = userClient.getUserById(order.getBuyerId());
        orderDTO.setBuyerNick(user.getNick());
        orderDTO.setBuyerMemo(order.getBuyerMemo());
        orderDTO.setSellerId(order.getSellerId());
        orderDTO.setSellerMemo(order.getSellerMemo());
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



    @Transactional
    public void shipOrder(ShipOrderParam shipOrderParam) {
        Long orderId = shipOrderParam.getId();
        Order order = getOrderSafely(orderId);
        Integer status = order.getStatus();
        String logisticsCompany = shipOrderParam.getLogisticsCompany();
        String trackingNumber = shipOrderParam.getTrackingNumber();
        order.setLogisticsCompany(logisticsCompany);
        order.setTrackingNumber(trackingNumber);
        order.setStatus(Order.STATUS_SHIPPED);

        if (!status.equals(Order.STATUS_PAID) || orderRepository.updateStatus(order, status) != 1)
            throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());

        List<OrderItem> orderItems = orderItemRepository.getByOrderId(orderId);
        orderItems.forEach(orderItem -> {
            //订单项有可能被退款而关闭，只对已付款状态的订单项进行发货
            Integer itemStatus = orderItem.getStatus();
            if (Order.STATUS_PAID.equals(orderItem.getTitle())) {
                orderItem.setStatus(Order.STATUS_SHIPPED);
                orderItem.setLogisticsCompany(logisticsCompany);
                orderItem.setTrackingNumber(trackingNumber);
                //并发情况下，订单项有可能被关闭了！
                if (orderItemRepository.update(orderItem, itemStatus) != 1)
                    throw new BusinessException(TradeErrorCode.ORDER_INVALID_STATUS, orderId.toString());
            }
        });


        saveOrderLog(order, "发货", "物流公司：" + logisticsCompany + "," + trackingNumber);
    }

    public int updateSellerMemo(Long orderId, String sellerMemo) {
        Order order = getOrderSafely(orderId);
        order.setSellerMemo(sellerMemo);
        return orderRepository.updateSellerMemo(order);
    }

    private Order getOrderSafely(Long orderId) {
        if (orderId == null)
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, "null");
        Order order = orderRepository.getById(orderId);
        if (order == null)
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, orderId.toString());

        Long userId = authenticationService.getCurrentUserId();
        //只能访问自己的订单
        if (!order.getSellerId().equals(userId))
            throw new BusinessException(TradeErrorCode.ORDER_ILLEGAL_ACCESS, orderId.toString());

        return order;
    }

    private void saveOrderLog(Order order, String operation, String message) {
        //记录订单流水
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(order.getId());
        orderLog.setOrderStatus(order.getStatus());
        orderLog.setOperator("卖家：" + authenticationService.getCurrentUserId());
        orderLog.setOperation(operation);
        orderLog.setMessage(message);
        orderLogRepository.create(orderLog);
    }
}
