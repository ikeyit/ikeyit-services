package com.ikeyit.trade.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.common.utils.JsonUtils;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.trade.domain.Order;
import com.ikeyit.trade.domain.OrderItem;
import com.ikeyit.trade.domain.Refund;
import com.ikeyit.trade.domain.RefundNegotiation;
import com.ikeyit.trade.dto.CreateRefundParam;
import com.ikeyit.trade.dto.RefundDTO;
import com.ikeyit.trade.dto.ShipReturnParam;
import com.ikeyit.trade.dto.UpdateRefundParam;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.repository.OrderItemRepository;
import com.ikeyit.trade.repository.OrderRepository;
import com.ikeyit.trade.repository.RefundNegotiationRepository;
import com.ikeyit.trade.repository.RefundRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BuyerRefundService {
    @Autowired
    RefundRepository refundRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    RefundNegotiationRepository refundNegotiationRepository;

    @Autowired
    AuthenticationService authenticationService;

    public RefundDTO getRefundApplication(Long orderItemId) {
        OrderItem orderItem = getRefundableOrderItem(orderItemId);
        RefundDTO refund = new RefundDTO();
        refund.setProductId(orderItem.getProductId());
        refund.setOrderItemId(orderItemId);
        refund.setTitle(orderItem.getTitle());
        refund.setImage(orderItem.getImage());
        refund.setPaymentAmount(orderItem.getPaymentAmount());
        refund.setPrice(orderItem.getPrice());
        refund.setQuantity(orderItem.getQuantity());
        refund.setOrderId(orderItem.getOrderId());
        refund.setOrderStatus(orderItem.getStatus());
        refund.setSkuAttributes(orderItem.getSkuAttributes());
        refund.setAmount(getMaxAmount(orderItem.getPaymentAmount(), orderItem.getQuantity()));
        return refund;
    }

    private BigDecimal getMaxAmount(BigDecimal paymentAmount, Integer quantity) {
        return paymentAmount.multiply(new BigDecimal(quantity));
    }

    private void validateAmount(BigDecimal paymentAmount, Integer quantity, BigDecimal amount) {
        BigDecimal maxAmount = getMaxAmount(paymentAmount, quantity);
        if (BigDecimal.ZERO.compareTo(amount) > 0 || maxAmount.compareTo(amount) < 0)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
    }

    private OrderItem getRefundableOrderItem(Long orderItemId) {
        Long userId = authenticationService.getCurrentUserId();
        OrderItem orderItem = orderItemRepository.getById(orderItemId);
        if (orderItem == null)
            throw new BusinessException(TradeErrorCode.ORDER_NOT_FOUND, orderItemId);
        if (!orderItem.getBuyerId().equals(userId))
            throw new BusinessException(TradeErrorCode.ORDER_ILLEGAL_ACCESS, orderItemId);

        // TODO 校验是否可以创建售后单
        Integer orderStatus = orderItem.getStatus();
        boolean canRefund = Order.STATUS_PAID.equals(orderStatus) ||
            Order.STATUS_SHIPPED.equals(orderStatus) ||
            Order.STATUS_FINISHED.equals(orderStatus);
        if (!canRefund)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, orderItemId);
        return orderItem;
    }

    /**
     * 买家提交退货退款申请
     */
    @Transactional
    public RefundDTO createRefund(CreateRefundParam createRefundParam) {
        Long orderItemId = createRefundParam.getOrderItemId();
        OrderItem orderItem = getRefundableOrderItem(orderItemId);

        // 校验退款退货单是否已经存在
        Refund refund = refundRepository.getStatusIsNot(orderItemId, Refund.STATUS_CLOSED);
        if (refund != null)
            throw new BusinessException(TradeErrorCode.REFUND_EXISTING, refund.getId());


        Integer refundType = createRefundParam.getRefundType();
//        未发货时,只允许仅退款
//        if (Order.STATUS_PAID.equals(orderItem.getStatus()) && !Refund.TYPE_ONLY_REFUND.equals(refundType)) {
//            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);
//        }

        String reason = createRefundParam.getReason();
        BigDecimal amount = createRefundParam.getAmount();
        if (refundType == null || StringUtils.isBlank(reason) || amount == null)
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT);

        validateAmount(orderItem.getPaymentAmount(), orderItem.getQuantity(), amount);

        refund = new Refund();
        refund.setRefundType(refundType);
        refund.setReason(reason);
        refund.setMemo(createRefundParam.getMemo());
        refund.setAmount(amount);
        refund.setOrderId(orderItem.getOrderId());
        refund.setOrderItemId(orderItem.getId());
        refund.setBuyerId(orderItem.getBuyerId());
        refund.setSellerId(orderItem.getSellerId());
        refund.setProductId(orderItem.getProductId());
        refund.setSkuId(orderItem.getSkuId());
        refund.setSkuCode(orderItem.getSkuCode());
        refund.setSkuAttributes(orderItem.getSkuAttributes());
        refund.setTitle(orderItem.getTitle());
        refund.setImage(orderItem.getImage());
        refund.setQuantity(orderItem.getQuantity());
        refund.setPaymentAmount(orderItem.getPaymentAmount());
        refund.setPrice(orderItem.getPrice());
        refund.setStatus(Refund.STATUS_WAIT_SELLER_AGREE);
        refundRepository.create(refund);

        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("退款类型", refund.getRefundType());
        message.put("退货理由", refund.getReason());
        message.put("备注说明", refund.getMemo());
        saveRefundNegotiation(refund.getId(), "提交申请", message);

        RefundDTO refundDTO = new RefundDTO();
        refundDTO.setId(refund.getId());
        return refundDTO;
    }

    /**
     * 买家寄出退货
     *
     */
    @Transactional
    public void shipReturn(ShipReturnParam shipReturnParam) {
        Long refundId = shipReturnParam.getId();
        Refund refund = getRefund(refundId);
        Integer nowStatus = refund.getStatus();
        if (!nowStatus.equals(Refund.STATUS_WAIT_BUYER_RETURN_GOODS))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        refund.setLogisticsCompany(shipReturnParam.getLogisticsCompany());
        refund.setTrackingNumber(shipReturnParam.getTrackingNumber());
        refund.setStatus(Refund.STATUS_WAIT_SELLER_CONFIRM_GOODS);

        if (refundRepository.updateStatus(refund, nowStatus) != 1)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("留言", shipReturnParam.getMemo());
        message.put("物流公司", refund.getLogisticsCompany());
        message.put("物流单号", refund.getTrackingNumber());
        saveRefundNegotiation(refund.getId(), "寄出退货", message);
    }

    /**
     * 买家取消退货退款申请
     * @param refundId
     */
    @Transactional
    public void cancelRefund(Long refundId) {
        Refund refund = getRefund(refundId);
        Integer nowStatus = refund.getStatus();
        if (!nowStatus.equals(Refund.STATUS_WAIT_SELLER_AGREE) && !nowStatus.equals(Refund.STATUS_WAIT_BUYER_RETURN_GOODS))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        refund.setStatus(Refund.STATUS_CLOSED);
        if (refundRepository.updateStatus(refund, nowStatus) != 1)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        saveRefundNegotiation(refundId, "取消申请", null);
    }

    /**
     * 买家更新退货退款申请
     * @return
     */
    @Transactional
    public int updateRefund(UpdateRefundParam updateRefundParam) {
        Long refundId = updateRefundParam.getRefundId();
        Refund refund = getRefund(refundId);
        Integer nowStatus = refund.getStatus();
        //等待卖家同意或卖家拒绝时，可以更新
        if (!nowStatus.equals(Refund.STATUS_WAIT_SELLER_AGREE) && !nowStatus.equals(Refund.STATUS_SELLER_REFUSE_BUYER))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);
        validateAmount(refund.getPaymentAmount(), refund.getQuantity(), updateRefundParam.getAmount());
        refund.setReason(updateRefundParam.getReason());
        refund.setAmount(updateRefundParam.getAmount());
        refund.setMemo(updateRefundParam.getMemo());
        refund.setStatus(Refund.STATUS_WAIT_SELLER_AGREE);
        if (refundRepository.updateStatus(refund, nowStatus) != 1)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("退款类型", refund.getRefundType());
        message.put("退货理由", refund.getReason());
        message.put("备注说明", refund.getMemo());
        saveRefundNegotiation(refund.getId(), "修改请求", message);
        return 1;
    }

    public RefundDTO getRefundDetail(Long refundId) {
        Refund refund = getRefund(refundId);
        OrderItem orderItem = orderItemRepository.getById(refund.getOrderItemId());
        Integer orderStatus = orderItem.getStatus();

        RefundDTO refundDTO = new RefundDTO();
        refundDTO.setId(refund.getId());
        refundDTO.setProductId(refund.getProductId());
        refundDTO.setTitle(refund.getTitle());
        refundDTO.setImage(refund.getImage());
        refundDTO.setSkuAttributes(refund.getSkuAttributes());
        refundDTO.setPrice(refund.getPrice());
        refundDTO.setPaymentAmount(refund.getPaymentAmount());
        refundDTO.setQuantity(refund.getQuantity());

        refundDTO.setOrderId(refund.getOrderId());
        refundDTO.setOrderItemId(refund.getOrderItemId());
        refundDTO.setOrderStatus(orderStatus);

        refundDTO.setStatus(refund.getStatus());
        refundDTO.setRefundType(refund.getRefundType());
        refundDTO.setReason(refund.getReason());
        refundDTO.setMemo(refund.getMemo());
        refundDTO.setAmount(refund.getAmount());
        refundDTO.setLogisticsCompany(refund.getLogisticsCompany());
        refundDTO.setTrackingNumber(refund.getTrackingNumber());
        refundDTO.setShipTime(refund.getShipTime());
        refundDTO.setCreateTime(refund.getCreateTime());
        refundDTO.setFinishTime(refund.getFinishTime());

        List<RefundNegotiation> negotiations = refundNegotiationRepository.listByRefundId(refundId);
        refundDTO.setNegotiations(negotiations.stream().map(RefundUtils::convert).collect(Collectors.toList()));
        return refundDTO;
    }

    /**
     * 买家的退货退款列表
     * @param pageParam
     * @return
     */
    public Page<Refund> getRefunds(PageParam pageParam) {
        Long userId = authenticationService.getCurrentUserId();
        return refundRepository.getByBuyer(userId, pageParam);
    }


    /**
     * 退货退款申请
     * @param refundId
     * @return
     */
    private Refund getRefund(Long refundId) {
        Long userId = authenticationService.getCurrentUserId();
        Refund refund = refundRepository.getById(refundId);
        if (refund == null)
            throw new BusinessException(TradeErrorCode.REFUND_NOT_FOUND, refundId);
        if (!refund.getBuyerId().equals(userId))
            throw new BusinessException(TradeErrorCode.REFUND_ILLEGAL_ACCESS, refundId);

        return refund;
    }


    private void saveRefundNegotiation(Long refundId, String operation, LinkedHashMap message) {
        Long userId = authenticationService.getCurrentUserId();
        RefundNegotiation refundNegotiation = new RefundNegotiation();
        refundNegotiation.setRefundId(refundId);
        refundNegotiation.setOperatorId(userId);
        refundNegotiation.setOperator("buyer");
        refundNegotiation.setOperation(operation);
        refundNegotiation.setMessage(JsonUtils.writeValueAsString(message));
        refundNegotiationRepository.create(refundNegotiation);
    }
}
