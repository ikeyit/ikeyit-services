package com.ikeyit.pay.service.impl.wexinpay;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import com.ikeyit.pay.domain.PayOrder;
import com.ikeyit.pay.domain.RefundOrder;
import com.ikeyit.pay.dto.PayResult;
import com.ikeyit.pay.dto.RefundResult;
import com.ikeyit.pay.service.PayCallback;
import com.ikeyit.pay.service.PayProvider;
import com.ikeyit.pay.feign.ConnectionClient;
import com.ikeyit.pay.feign.WeixinConnectionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class WeixinPayProvider implements PayProvider, WeixinPayListener {

    private static Logger log = LoggerFactory.getLogger(WeixinPayProvider.class);

    private static final BigDecimal AMOUNT_CONVERSION = new BigDecimal(100);

    @Autowired
    private PayCallback payCallback;

    @Value("${weixinpay.name:weixinpay}")
    private String name="weixinpay";

    // 商户号
    @Value("${weixinpay.merchantId}")
    private String merchantId;
    // 商户证书序列号
    @Value("${weixinpay.merchantSerialNo}")
    private String merchantSerialNo;
    // 商户私钥perm文件路径
    @Value("${weixinpay.merchantPrivateKeyFile}")
    private String merchantPrivateKeyFile;
    // api v3密钥
    @Value("${weixinpay.apiV3Key}")
    private String apiV3Key;

    @Value("${weixinpay.notifyBaseUrl}")
    private String notifyBaseUrl;

    @Autowired
    ConnectionClient connectionClient;

    @Autowired
    ObjectMapper objectMapper;

    WeixinPayClient weixinPayClient;

    public WeixinPayProvider() {

    }

    @PostConstruct
    protected void init() {
        weixinPayClient = new WeixinPayClient(merchantId, merchantSerialNo, merchantPrivateKeyFile, apiV3Key, notifyBaseUrl);
    }

    public Object handleNotify(RequestEntity<String> httpEntity) {
        return weixinPayClient.handleNotify(httpEntity, this);
    }

    @Override
    public void onNotify(String eventType, ObjectNode data, String notificationStr) {
        log.debug("微信支付回调! 消息类型: {}, 数据: {}", eventType, notificationStr);
        switch(eventType) {
            case "REFUND.SUCCESS":
                RefundResult refundResult = buildRefundResult(data);
                payCallback.handleRefundSuccess(refundResult);
                break;
            case "TRANSACTION.SUCCESS":
                PayResult payResult = buildPayResult(data);
                payCallback.handlePaySuccess(payResult);
                break;
            default:
                throw new IllegalStateException("微信回调没有对应处理逻辑");
        }
    }

    @Override
    public void setPayCallback(PayCallback payCallback) {
        this.payCallback = payCallback;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object requestPayment(PayOrder payOrder, Map<String, Object> params) {
        String type = (String) params.get("type");
        String appId = (String) params.get("appId");
        if ("jsapi".equals(type)) {
            return requestPaymentMiniProgram(appId, payOrder);
        }

        throw new BusinessException(CommonErrorCode.INVALID_ARGUMENT, "请求微信支付的参数不对！");
    }

    @Override
    public PayResult queryPayment(String trade_no) {
        ObjectNode data = weixinPayClient.queryTransaction(trade_no);
        log.debug("微信支付查询支付! 响应: {}", data);
        return buildPayResult(data);
    }

    @Override
    public void requestRefund(RefundOrder refundOrder) {
        ObjectNode request = objectMapper.createObjectNode();
        request.put("out_trade_no", refundOrder.getTradeNo());
        request.put("out_refund_no", refundOrder.getRefundNo());
        request.put("reason", refundOrder.getReason());
        ObjectNode amountNode = request.putObject("amount");
        amountNode.put("refund", refundOrder.getRefundAmount().multiply(AMOUNT_CONVERSION).toBigInteger());
        amountNode.put("total", refundOrder.getPaymentAmount().multiply(AMOUNT_CONVERSION).toBigInteger());
        amountNode.put("currency", "CNY");
        ObjectNode result = weixinPayClient.createRefund(request);
        log.debug("微信支付申请退款! 响应: {}", result);
    }

    @Override
    public RefundResult queryRefund(String refund_no) {
        return null;
    }

    private RefundResult buildRefundResult(ObjectNode data) {
        String out_refund_no = data.path("out_refund_no").asText();
        String out_trade_no = data.path("out_trade_no").asText();
        String transaction_id = data.path("transaction_id").asText();
        String refund_id = data.path("refund_id").asText();
        LocalDateTime success_time = LocalDateTime.parse(data.path("success_time").asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        JsonNode amountNode = data.path("amount");
        BigDecimal paymentAmount = new BigDecimal(amountNode.path("total").asLong()).divide(AMOUNT_CONVERSION);
        BigDecimal refundAmount = new BigDecimal(amountNode.path("refund").asLong()).divide(AMOUNT_CONVERSION);
        RefundResult refundResult = new RefundResult();
        refundResult.setRefundNo(out_refund_no);
        refundResult.setTradeNo(out_trade_no);
        refundResult.setTransactionId(transaction_id);
        refundResult.setSuccessTime(success_time);
        refundResult.setStatus(RefundOrder.STATUS_SUCCESS);
//        refundResult.setPaymentAmount(paymentAmount);
        refundResult.setRefundAmount(refundAmount);
        refundResult.setRefundData(data.toString());
        return refundResult;
    }

    private PayResult buildPayResult(ObjectNode data) {

        String trade_state = data.path("trade_state").asText();
        String out_trade_no = data.path("out_trade_no").asText();
        String transaction_id = data.path("transaction_id").asText();
        LocalDateTime success_time = null;
        if (data.has("success_time"))
            success_time = LocalDateTime.parse(data.get("success_time").asText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        JsonNode amountNode = data.path("amount");
        BigDecimal paymentAmount = new BigDecimal(amountNode.path("total").asLong()).divide(AMOUNT_CONVERSION);
        PayResult payResult = new PayResult();
        if ("SUCCESS".equals(trade_state))
            payResult.setStatus(PayResult.STATUS_SUCCESS);
        else
            payResult.setStatus(PayResult.STATUS_UNPAID);
        payResult.setPayWay(name);
        payResult.setTradeNo(out_trade_no);
        payResult.setPaymentAmount(paymentAmount);
        payResult.setSuccessTime(success_time);
        payResult.setTransactionId(transaction_id);
        payResult.setTransactionData(data.toString());
        payResult.setSuccessTime(success_time);
        return payResult;
    }


    private ObjectNode requestPaymentMiniProgram(String appId, PayOrder payOrder) {
        WeixinConnectionDTO weixinConnection = connectionClient.getWeixinConnection(appId);
        String openId = weixinConnection.getOpenId();
        ObjectNode request = objectMapper.createObjectNode();
        request.putObject("amount")
                .put("total", payOrder.getPaymentAmount().multiply(AMOUNT_CONVERSION).toBigInteger())
                .put("currency", "CNY");
        request.put("appid", appId);
        request.put("description", payOrder.getSubject());
        request.put("out_trade_no", payOrder.getTradeNo());
        LocalDateTime expireTime = payOrder.getExpireTime();
        if (expireTime != null)
            request.put("time_expire", expireTime.atZone(ZoneOffset.ofHours(8)).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        request.putObject("payer").put("openid", openId);
        request.putObject("scene_info").put("payer_client_ip", payOrder.getClientIp());
        return weixinPayClient.requestPaymentMiniProgram(request);
    }
}
