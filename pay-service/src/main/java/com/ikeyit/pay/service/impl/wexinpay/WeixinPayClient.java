package com.ikeyit.pay.service.impl.wexinpay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.ikeyit.common.exception.BusinessException;
import com.ikeyit.common.exception.CommonErrorCode;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * 对微信支付进行封装， 不含任何业务逻辑
 */
public class WeixinPayClient implements ResourceLoaderAware {

    private static final Logger log = LoggerFactory.getLogger(WeixinPayClient.class);

    private static final String URL_CREATE_ORDER = "https://api.mch.weixin.qq.com/v3/pay/transactions/";

    private static final String URL_ORDER_QUERY = "https://api.mch.weixin.qq.com/v3/pay/transactions/id/";

    private static final String URL_SEND_RED_PACK = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

    private static final String URL_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    private static final String URL_REFUND_QUERY = "https://api.mch.weixin.qq.com/pay/refundquery";

    private static final String CONTENT_TYPE = "application/json; charset=utf-8";
    // 商户号
    private String merchantId;

    // 商户证书序列号
    private String merchantSerialNo;

    // 商户私钥perm文件路径
    private String merchantPrivateKeyFile;

    // api v3密钥
    private String apiV3Key;

    // 回调地址
    private String notifyBaseUrl;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private ObjectMapper objectMapper = new ObjectMapper();

    private CloseableHttpClient httpClient;

    //自动更新微信平台证书，用平台证书验证签名合法性
    private AutoUpdateCertificatesVerifier weixinVerifier;

    //商户私钥
    private PrivateKey merchantPrivateKey;

    private AesUtil apiV3KeyAesUtil;

    public WeixinPayClient(String merchantId, String merchantSerialNo, String merchantPrivateKeyFile, String apiV3Key, String notifyBaseUrl) {
        this.merchantId = merchantId;
        this.merchantSerialNo = merchantSerialNo;
        this.merchantPrivateKeyFile = merchantPrivateKeyFile;
        this.apiV3Key = apiV3Key;
        this.notifyBaseUrl = notifyBaseUrl;
        init();
    }


    private void init() {
        try (InputStream inputStream = resourceLoader.getResource(merchantPrivateKeyFile).getInputStream()) {
            merchantPrivateKey = PemUtil.loadPrivateKey(inputStream);
            weixinVerifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(merchantId, new PrivateKeySigner(merchantSerialNo, merchantPrivateKey)),
                    apiV3Key.getBytes(StandardCharsets.UTF_8));
            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(merchantId, merchantSerialNo, merchantPrivateKey)
                    .withValidator(new WechatPay2Validator(weixinVerifier));
            apiV3KeyAesUtil = new AesUtil(apiV3Key.getBytes(StandardCharsets.UTF_8));
            httpClient = builder.build();
        } catch(IOException e) {
            log.error("微信支付API环境创建失败！微信支付不可用！", e);
        }
    }


    private String timeStamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    private String nonceStr() {
        return RandomStringUtils.random(32, true, true);
    }

    //商户签名
    private String merchantSign(byte[] message) {
        try {
            Signature sign = Signature.getInstance("SHA256withRSA");
            sign.initSign(merchantPrivateKey);
            sign.update(message);
            return Base64.getEncoder().encodeToString(sign.sign());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持SHA256withRSA", e);
        } catch (SignatureException e) {
            throw new RuntimeException("签名计算失败", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("无效的私钥", e);
        }
    }

    //验证微信回调请求
    private boolean validateNotify(HttpEntity<String> httpEntity) {
        HttpHeaders headers = httpEntity.getHeaders();
        String timestamp = headers.getFirst("Wechatpay-Timestamp");
        String nonce = headers.getFirst("Wechatpay-Nonce");
        String serial = headers.getFirst("Wechatpay-Serial");
        String signature = headers.getFirst("Wechatpay-Signature");
        String body = httpEntity.getBody();
        String message = timestamp + "\n" + nonce + "\n" + body + "\n";

        if (!weixinVerifier.verify(serial, message.getBytes(StandardCharsets.UTF_8), signature)) {
            String errorMsg = String.format("serial=[%s] message=[%s] sign=[%s], request-id=[%s]",
                    serial, message, signature, headers.getFirst("Request-ID"));
            log.error("微信回调验签失败：" + errorMsg);
            return false;
        }

        return true;
    }

    private CloseableHttpClient getHttpClient() {
        if (httpClient == null)
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR, "微信支付服务不可用！");
        return httpClient;
    }

    private ObjectNode get(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", ContentType.APPLICATION_JSON.toString());
        return execute(httpGet);
    }

    private ObjectNode post(String url, ObjectNode request) {
        HttpPost httpPost = new HttpPost(url);
        String requestStr = null;
        try {
            requestStr = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            //应该不会发生
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR, e, "无法序列化json");
        }

        StringEntity entity = new StringEntity(requestStr, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", ContentType.APPLICATION_JSON.toString());
        return execute(httpPost);
    }

    private ObjectNode execute(HttpUriRequest request) {
        try(CloseableHttpResponse response = getHttpClient().execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseStr = EntityUtils.toString(response.getEntity());
            ObjectNode responseJson = null;
            if (StringUtils.hasText(responseStr))
                responseJson = (ObjectNode) objectMapper.readTree(responseStr);

            if (statusCode >= 200 && statusCode < 300)
                return responseJson;
            if (statusCode >= 400 && statusCode < 500)
                //我方问题
                throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR,
                        responseJson.get("message") + "(" + responseJson.get("code") + ")");

            //微信方问题
            throw new BusinessException(CommonErrorCode.THIRD_PARTY_ERROR,
                    responseJson.get("message") + "(" + responseJson.get("code") + ")");
        } catch(IOException e) {
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR, e, "调用微信支付接口失败");
        }
    }


    public ObjectNode createTransaction(String type, ObjectNode request) {
        request.put("mchid", merchantId);
        request.put("notify_url", notifyBaseUrl + "/weixinpay_notify");
        return post(URL_CREATE_ORDER + type, request);
    }

    public ObjectNode requestPaymentMiniProgram(ObjectNode request) {
        ObjectNode createOrderResponse = createTransaction("jsapi", request);
        String prepay_id = createOrderResponse.path("prepay_id").asText();
        String appId = request.path("appid").asText();
        String timeStamp = timeStamp();
        String nonceStr = nonceStr();
        String packageStr = "prepay_id=" + prepay_id;
        String data = appId + "\n"+ timeStamp + "\n" + nonceStr + "\n" + packageStr + "\n";
        String paySign = merchantSign(data.getBytes(StandardCharsets.UTF_8));
        ObjectNode response = objectMapper.createObjectNode();
        response.put("timeStamp", timeStamp);
        response.put("nonceStr", nonceStr);
        response.put("package", packageStr);
        response.put("signType", "RSA");
        response.put("paySign", paySign);
        return response;
    }

    public ObjectNode requestPaymentApp(ObjectNode request) {
        ObjectNode createOrderResponse = createTransaction("app", request);
        String prepay_id = createOrderResponse.path("prepay_id").asText();
        String appId = request.path("appid").asText();
        String timeStamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = RandomStringUtils.random(32, true, true);
        String packageStr = prepay_id;
        String data = appId + "\n"+ timeStamp + "\n" + nonceStr + "\n" + packageStr + "\n";
        String sign = merchantSign(data.getBytes(StandardCharsets.UTF_8));
        ObjectNode response = objectMapper.createObjectNode();
        response.put("timeStamp", timeStamp);
        response.put("nonceStr", nonceStr);
        response.put("prepayId", prepay_id);
        response.put("sign", sign);
        return response;
    }

    public ObjectNode queryTransaction(String out_trade_no) {
        return get("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/" + out_trade_no + "?mchid=" + merchantId);
    }

    public ObjectNode createRefund(ObjectNode request) {
        request.put("notify_url", notifyBaseUrl + "/weixinpay_notify");
        return post("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds", request);
    }

    public ResponseEntity<?> handleNotify(RequestEntity<String> httpEntity, WeixinPayListener listener) {
        if (!validateNotify(httpEntity))
            return buildNotifyResponse("INVALIDATE_SIGNATURE", "签名验证失败", HttpStatus.BAD_REQUEST);

        ObjectNode notification = null;
        String notificationStr = null;
        String event_type = null;
        try {
            ObjectNode request = (ObjectNode) objectMapper.readTree(httpEntity.getBody());
//            String id = request.path("id").asText();
            event_type = request.path("event_type").asText();
            String resource_type = request.path("resource_type").asText();
            JsonNode resourceNode = request.path("resource");
            String nonce = resourceNode.path("nonce").asText();
            String associated_data = resourceNode.path("associated_data").asText();
            String cipherext = resourceNode.path("ciphertext").asText();
            notificationStr = apiV3KeyAesUtil.decryptToString(
                    associated_data.getBytes(StandardCharsets.UTF_8),
                    nonce.getBytes(StandardCharsets.UTF_8),
                    cipherext);
             notification = (ObjectNode) objectMapper.readTree(notificationStr);
        } catch (Exception e) {
            //解码通知失败
            log.error("解析微信通知失败", e);
            return buildNotifyResponse("DECODE_FAIL", "解析微信通知失败", HttpStatus.BAD_REQUEST);
        }

        try {
            if (listener != null) {
                listener.onNotify(event_type, notification, notificationStr);
                return buildNotifyResponse("SUCCESS", null, HttpStatus.OK);
            } else {
                log.warn("没有微信通知listener处理该通知：" + notificationStr);
                return buildNotifyResponse("INTERNAL_SERVER_ERROR", "系统没有实现对该通知的处理！", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return buildNotifyResponse("INTERNAL_SERVER_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> buildNotifyResponse(String code, String message, HttpStatus status) {
        ObjectNode response = objectMapper.createObjectNode();
        response.put("code", code);
        if (message != null)
            response.put("message", message);
        return new ResponseEntity<>(response, status);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
