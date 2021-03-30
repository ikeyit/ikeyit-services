package com.ikeyit.pay.service.impl;

import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.mqhelper.MqSender;
import com.ikeyit.pay.domain.PayOrder;
import com.ikeyit.pay.domain.RefundOrder;
import com.ikeyit.pay.dto.*;
import com.ikeyit.pay.exception.PayErrorCode;
import com.ikeyit.pay.mq.PaySuccessMessage;
import com.ikeyit.pay.mq.RefundSubmitMessage;
import com.ikeyit.pay.mq.RefundSuccessMessage;
import com.ikeyit.pay.repository.PayOrderRepository;
import com.ikeyit.pay.repository.RefundOrderRepository;
import com.ikeyit.pay.service.PayCallback;
import com.ikeyit.pay.service.PayProvider;
import com.ikeyit.pay.service.PayProviderManager;
import com.ikeyit.pay.service.PayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 统一的支付入口，屏蔽第三方差异。
 */
@Service
public class PayServiceImpl implements PayService, PayCallback {

    private static final String MQ_KEY_PREFIX_PAY_ORDER = "pay_order-";

    private static final String MQ_KEY_PREFIX_REFUND_ORDER = "refund_order-";

    private static final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    PayProviderManager payProviderManager;

    @Autowired
    PayOrderRepository payOrderRepository;

    @Autowired
    RefundOrderRepository refundOrderRepository;

    @Autowired
    MqSender mqSender;

    String prefix = "test_";

    /**
     * 第三方支付成功回调处理！
     * @param payResult
     */
    @Override
    @Transactional
    public void handlePaySuccess(PayResult payResult) {
        PayOrder payOrder = payOrderRepository.getByTradeNo(payResult.getTradeNo());
        if (payOrder == null) {
            log.error("[潜在bug]收到支付成功通知，更新支付订单，但没有找到支付订单！tradeNo：{}", payResult.getTradeNo());
            return;
        }

        Integer status = payOrder.getStatus();
        if (PayOrder.STATUS_PAID.equals(status)) {
            log.debug("收到支付成功通知，但支付订单状态已经为已支付，直接返回！payOrderId: {}", payOrder.getId());
            return;
        }
        payOrder.setPayWay(payOrder.getPayWay());
        payOrder.setSuccessTime(payResult.getSuccessTime());
        payOrder.setTransactionId(payOrder.getTransactionId());
        payOrder.setTransactionData(payResult.getTransactionData());
        payOrder.setStatus(PayOrder.STATUS_PAID);
        if (payOrderRepository.update(payOrder, status) != 1) {
            //抛异常，引起第三方支付回调重试
            throw new IllegalStateException("我方支付订单状态不对！需要第三方重发消息");
        }

        PaySuccessMessage paySuccessMessage = new PaySuccessMessage();
        paySuccessMessage.setOrderType(payOrder.getOrderType());
        paySuccessMessage.setOrderId(payOrder.getOrderId());
        paySuccessMessage.setPaymentAmount(payOrder.getPaymentAmount());
        paySuccessMessage.setPayOrderId(payOrder.getId());
        paySuccessMessage.setPayWay(payOrder.getPayWay());
        paySuccessMessage.setSuccessTime(payOrder.getSuccessTime());
        mqSender.asyncSendAfterCommit("pay_success:" + payOrder.getOrderType(), paySuccessMessage, MQ_KEY_PREFIX_PAY_ORDER + payOrder.getId());
    }

    /**
     * 响应第三方退款成功回调
     * @param refundResult
     */
    @Override
    @Transactional
    public void handleRefundSuccess(RefundResult refundResult) {
        RefundOrder refundOrder = refundOrderRepository.getByRefundNo(refundResult.getRefundNo());
        Integer status = refundOrder.getStatus();
        if (RefundOrder.STATUS_SUCCESS.equals(status))
            return;
        refundOrder.setSuccessTime(refundResult.getSuccessTime());
        refundOrder.setRefundData(refundResult.getRefundData());
        refundOrder.setStatus(RefundOrder.STATUS_SUCCESS);
        if (refundOrderRepository.update(refundOrder, status) != 1)
            return;
        if (payOrderRepository.addRefundAmount(refundOrder.getPayOrderId(), refundOrder.getRefundAmount()) !=1) {
            log.error("[潜在BUG]退款金额超过了付款金额，refundOrderId: {}", refundOrder.getId());
            throw new IllegalStateException("退款金额超过了付款金额");
        }

        RefundSuccessMessage refundSuccessMessage = new RefundSuccessMessage();
        refundSuccessMessage.setPayOrderId(refundOrder.getPayOrderId());
        refundSuccessMessage.setRefundOrderId(refundOrder.getId());
        refundSuccessMessage.setRefundAmount(refundOrder.getRefundAmount());
        refundSuccessMessage.setRefundType(refundOrder.getRefundType());
        refundSuccessMessage.setRefundId(refundOrder.getRefundId());
        refundSuccessMessage.setPayWay(refundOrder.getPayWay());
        refundSuccessMessage.setSuccessTime(refundResult.getSuccessTime());
        mqSender.asyncSendAfterCommit("pay_refund_success:" + refundOrder.getRefundType(), refundSuccessMessage, MQ_KEY_PREFIX_REFUND_ORDER + refundOrder.getId());
    }

    /**
     * 创建支付订单
     * @param createPayOrderParam
     * @return
     */
    @Override
    public Long createPayOrder(CreatePayOrderParam createPayOrderParam) {
        //TODO 参数验证
        Long orderId = createPayOrderParam.getOrderId();
        String orderType = createPayOrderParam.getOrderType();
        //创建
        PayOrder payOrder = new PayOrder();
        payOrder.setBuyerId(createPayOrderParam.getBuyerId());
        payOrder.setOrderType(orderType);
        payOrder.setOrderId(orderId);
        payOrder.setStatus(PayOrder.STATUS_UNPAID);
        payOrder.setTradeNo(prefix + orderType + orderId);
        payOrder.setPayWay(createPayOrderParam.getPayWay());
        payOrder.setPaymentAmount(createPayOrderParam.getPaymentAmount());
        payOrder.setRefundAmount(BigDecimal.ZERO);
        payOrder.setSubject(createPayOrderParam.getSubject());
        payOrder.setExpireTime(createPayOrderParam.getExpireTime());
        payOrderRepository.create(payOrder);
        return payOrder.getId();
    }

    /**
     * 提交支付订单给第三方支付系统
     * @param submitPayOrderParam
     * @return
     */
    @Override
    public Object submitPayOrder(SubmitPayOrderParam submitPayOrderParam) {
        String payWay = submitPayOrderParam.getPayWay();
        String orderType = submitPayOrderParam.getOrderType();
        Long orderId = submitPayOrderParam.getOrderId();
        PayProvider payProvider = payProviderManager.get(payWay);
        PayOrder payOrder = payOrderRepository.getByOrder(orderType, orderId);
        if (payOrder == null)
            throw new BusinessException(PayErrorCode.PAY_ORDER_NOT_FOUND);
        if (PayOrder.STATUS_PAID.equals(payOrder.getStatus()))
            throw new BusinessException(PayErrorCode.PAY_ORDER_PAID);
        if (LocalDateTime.now().isAfter(payOrder.getExpireTime()))
            throw new BusinessException(PayErrorCode.PAY_ORDER_EXPIRED);

        payOrder.setClientIp(submitPayOrderParam.getClientIp());
        return payProvider.requestPayment(payOrder, submitPayOrderParam.getParams());
    }

    /**
     * 查询支付订单
     * @param orderType
     * @param orderId
     * @return
     */
    @Override
    public PayOrder getPayOrder(String orderType, Long orderId) {
        return payOrderRepository.getByOrder(orderType, orderId);
    }

    /**
     * 主动同步第三方支付订单
     * @param orderType
     * @param orderId
     * @param payWay
     */
    @Override
    public void syncPayResult(String orderType, Long orderId, String payWay) {
        PayOrder payOrder = payOrderRepository.getByOrder(orderType, orderId);
        if (payOrder == null)
            throw new BusinessException(PayErrorCode.PAY_ORDER_NOT_FOUND);
        if (!PayOrder.STATUS_UNPAID.equals(payOrder.getStatus())) {
            log.debug("主动查询支付结果时，发现支付订单已经支付成功！跳过！payOrderId: {}", payOrder.getId());
            return;
        }
        PayProvider payProvider = payProviderManager.get(payWay);
        PayResult payResult = payProvider.queryPayment(payOrder.getTradeNo());
        if (PayResult.STATUS_SUCCESS.equals(payResult.getStatus())) {
            handlePaySuccess(payResult);
        }
    }

    /**
     * 创建退款订单
     * @param createRefundOrderParam
     * @return
     */
    @Override
    @Transactional
    public Long createRefundOrder(CreateRefundOrderParam createRefundOrderParam) {
        String refundType = createRefundOrderParam.getRefundType();
        Long refundId = createRefundOrderParam.getRefundId();
        Long orderId = createRefundOrderParam.getOrderId();
        String orderType = createRefundOrderParam.getOrderType();
        BigDecimal refundAmount = createRefundOrderParam.getRefundAmount();
        PayOrder payOrder = payOrderRepository.getByOrder(orderType, orderId);
        if (payOrder == null) {
            throw new BusinessException(PayErrorCode.PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrder.STATUS_PAID.equals(payOrder.getStatus())) {
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "状态不对");
        }
        if (payOrder.getRefundAmount().add(refundAmount).compareTo(payOrder.getPaymentAmount()) > 0) {
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "申请退款金额超过了支付金额");
        }

        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setPayOrderId(payOrder.getId());
        refundOrder.setRefundType(refundType);
        refundOrder.setRefundId(refundId);
        refundOrder.setStatus(RefundOrder.STATUS_CREATED);
        refundOrder.setRefundNo(prefix + refundType + refundId);
        refundOrder.setTradeNo(payOrder.getTradeNo());
        refundOrder.setReason(createRefundOrderParam.getReason());
        refundOrder.setPayWay(payOrder.getPayWay());
        refundOrder.setRefundAmount(refundAmount);
        refundOrder.setPaymentAmount(payOrder.getPaymentAmount());
        refundOrderRepository.create(refundOrder);

        RefundSubmitMessage refundSubmitMessage = new RefundSubmitMessage();
        refundSubmitMessage.setRefundType(refundType);
        refundSubmitMessage.setRefundId(refundId);
        mqSender.asyncSendAfterCommit("pay_refund_submit", refundSubmitMessage, MQ_KEY_PREFIX_REFUND_ORDER + refundOrder.getId());
        return refundOrder.getId();
    }

    /**
     * 提交退款订单给第三方支付
     * @param refundType
     * @param refundId
     */
    @Override
    @Transactional
    public void submitRefundOrder(String refundType, Long refundId) {
        RefundOrder refundOrder = refundOrderRepository.getByRefundId(refundType, refundId);
        if (refundOrder == null) {
            log.error("退款订单不存在, 可能存在BUG！refundType: {}, refundId: {}", refundType, refundId);
            throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "退款订单不存在！");
        }
        Integer status = refundOrder.getStatus();
        if (!RefundOrder.STATUS_CREATED.equals(status)) {
            log.warn("退款订单已经提交过了，直接返回！refundType: {}, refundId: {}", refundType, refundId);
            return;
        }

        refundOrder.setStatus(RefundOrder.STATUS_SUBMITTED);
        if (refundOrderRepository.update(refundOrder, status) != 1)
            //其它线程可能提交了请求，状态变化了！直接返回！
            return;

        //请求第三方支付进行退款
        String payWay = refundOrder.getPayWay();
        PayProvider payProvider = payProviderManager.get(payWay);
        payProvider.requestRefund(refundOrder);
    }

    /**
     * 查询退款订单
     * @param refundType
     * @param refundId
     * @return
     */
    @Override
    public RefundOrder getRefundOrder(String refundType, Long refundId) {
        return refundOrderRepository.getByRefundId(refundType, refundId);
    }
}
