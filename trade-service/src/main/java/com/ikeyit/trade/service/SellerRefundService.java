package com.ikeyit.trade.service;

import com.ikeyit.common.domain.Page;
import com.ikeyit.common.domain.PageParam;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.utils.JsonUtils;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.pay.dto.CreateRefundOrderParam;
import com.ikeyit.pay.service.PayService;
import com.ikeyit.trade.domain.OrderItem;
import com.ikeyit.trade.domain.Refund;
import com.ikeyit.trade.domain.RefundNegotiation;
import com.ikeyit.trade.domain.SellerAddress;
import com.ikeyit.trade.dto.AgreeReturnParam;
import com.ikeyit.trade.dto.RefundDTO;
import com.ikeyit.trade.dto.RejectRefundParam;
import com.ikeyit.trade.dto.SellerGetRefundsParam;
import com.ikeyit.trade.exception.TradeErrorCode;
import com.ikeyit.trade.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 卖家退货退款服务
 */
@Service
public class SellerRefundService {

    @Autowired
    RefundRepository refundRepository;

    @Autowired
    RefundNegotiationRepository refundNegotiationRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    SellerAddressRepository sellerAddressRepository;

    @Autowired
    PayService payService;

    /**
     * 卖家获取退款退货详情
     * @param refundId
     * @return
     */
    public RefundDTO getRefundDetail(Long refundId) {
        Refund refund = getRefund(refundId);
        RefundDTO refundDTO = buildRefundDTO(refund);
        List<RefundNegotiation> negotiations = refundNegotiationRepository.listByRefundId(refundId);
        refundDTO.setNegotiations(negotiations.stream().map(RefundUtils::convert).collect(Collectors.toList()));
        return refundDTO;
    }

    private RefundDTO buildRefundDTO(Refund refund) {
        OrderItem orderItem = orderItemRepository.getById(refund.getOrderItemId());
        Integer orderStatus = orderItem.getStatus();
        RefundDTO refundDTO = new RefundDTO();
        refundDTO.setId(refund.getId());
        refundDTO.setBuyerId(refund.getBuyerId());
        refundDTO.setProductId(refund.getProductId());
        refundDTO.setSkuId(refund.getSkuId());
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
        return refundDTO;
    }

    private Refund getRefund(Long refundId) {
        Long userId = authenticationService.getCurrentUserId();
        Refund refund = refundRepository.getById(refundId);
        if (refund == null)
            throw new BusinessException(TradeErrorCode.REFUND_NOT_FOUND, refundId);
        if (!refund.getSellerId().equals(userId))
            throw new BusinessException(TradeErrorCode.REFUND_ILLEGAL_ACCESS, refundId);
        return refund;
    }

    public Page<RefundDTO> getRefunds(SellerGetRefundsParam sellerGetRefundsParam,  PageParam pageParam) {
        Long userId = authenticationService.getCurrentUserId();
        Page<Refund> refunds = refundRepository.getBySeller(
                userId,
                sellerGetRefundsParam.getStatus(),
                sellerGetRefundsParam.getRefundType(),
                pageParam);
        return Page.map(refunds, refund -> buildRefundDTO(refund));
    }

    /**
     * 卖家同意退货退款申请
     */
    @Transactional
    public void agreeReturn(AgreeReturnParam agreeReturnParam) {
        Long refundId = agreeReturnParam.getId();
        Refund refund = getRefund(refundId);
        Integer status = refund.getStatus();
        if (!(Refund.TYPE_RETURN_REFUND.equals(refund.getRefundType())
                && Refund.STATUS_WAIT_SELLER_AGREE.equals(status)))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        refund.setStatus(Refund.STATUS_WAIT_BUYER_RETURN_GOODS);
        if (refundRepository.updateStatus(refund, status) != 1)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        SellerAddress address = sellerAddressRepository.getById(agreeReturnParam.getAddressId());
        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("留言", agreeReturnParam.getMemo());
        message.put("收件人", address.getName());
        message.put("电话", address.getPhone());
        message.put("退货地址", address.getProvince() + address.getCity() + address.getDistrict() + address.getStreet());
        saveRefundNegotiation(refundId, "同意退货", message);
    }

    /**
     * 卖家决绝退货退款申请
     */
    @Transactional
    public void rejectRefund(RejectRefundParam rejectRefundParam) {
        Long refundId = rejectRefundParam.getRefundId();
        Refund refund = getRefund(refundId);
        Integer status = refund.getStatus();
        if (!Refund.STATUS_WAIT_SELLER_AGREE.equals(status))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        refund.setStatus(Refund.STATUS_SELLER_REFUSE_BUYER);
        if (refundRepository.updateStatus(refund, status) != 1)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("拒绝理由", rejectRefundParam.getReason());
        message.put("拒绝说明", rejectRefundParam.getMemo());
        saveRefundNegotiation(refund.getId(), "拒绝请求", message);
    }


    /**
     * 卖家执行退款
     * @param refundId
     */
    @Transactional
    public void agreeRefund(Long refundId) {
        Refund refund = getRefund(refundId);
        Integer status = refund.getStatus();
        Integer refundType = refund.getRefundType();

        if (!(Refund.TYPE_RETURN_REFUND.equals(refundType) && Refund.STATUS_WAIT_SELLER_CONFIRM_GOODS.equals(status))
                && !(Refund.TYPE_ONLY_REFUND.equals(refundType) &&  Refund.STATUS_WAIT_SELLER_AGREE.equals(status)))
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        refund.setStatus(Refund.STATUS_SELLER_AGREED_REFUND);
        if (refundRepository.updateStatus(refund, status) != 1)
            throw new BusinessException(TradeErrorCode.REFUND_INVALID_STATUS, refundId);

        LinkedHashMap<String, Object> message = new LinkedHashMap<>();
        message.put("退款金额", refund.getAmount());
        message.put("系统备注", "系统1~2天内会将资金原路退回");
        saveRefundNegotiation(refundId, "同意退款", message);

        CreateRefundOrderParam createRefundOrderParam = new CreateRefundOrderParam();
        createRefundOrderParam.setRefundType(Constants.REFUND_ORDER_TYPE_AFTER_SALE);
        createRefundOrderParam.setRefundId(refund.getId());
        createRefundOrderParam.setRefundAmount(refund.getAmount());
        createRefundOrderParam.setReason(refund.getReason());
        createRefundOrderParam.setOrderType(Constants.PAY_ORDER_TYPE);
        createRefundOrderParam.setOrderId(refund.getOrderId());
        payService.createRefundOrder(createRefundOrderParam);
    }



    private void saveRefundNegotiation(Long refundId, String operation, LinkedHashMap message) {
        Long userId = authenticationService.getCurrentUserId();
        RefundNegotiation refundNegotiation = new RefundNegotiation();
        refundNegotiation.setRefundId(refundId);
        refundNegotiation.setOperatorId(userId);
        refundNegotiation.setOperator("seller");
        refundNegotiation.setOperation(operation);
        refundNegotiation.setMessage(JsonUtils.writeValueAsString(message));
        refundNegotiationRepository.create(refundNegotiation);
    }

}
