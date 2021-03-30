package com.ikeyit.pay.service.impl.wexinpay;

import com.ikeyit.common.utils.XmlUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.xml.transform.dom.DOMSource;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WeixinPayClientV2 {

    private static final Logger log = LoggerFactory.getLogger(WeixinPayClientV2.class);

    private static final String CHARSET = "UTF-8";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final DateTimeFormatter DATE_FORMAT_REDPACK = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final String URL_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    private static final String URL_ORDER_QUERY = "https://api.mch.weixin.qq.com/pay/orderquery";

    private static final String URL_SEND_RED_PACK = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

    private static final String URL_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    private static final String URL_REFUND_QUERY = "https://api.mch.weixin.qq.com/pay/refundquery";

    private static final String TAG_SIGN = "sign";

    WeixinPayListenerV2 weixinPayHandler;

    @Value("${WeixinPayService.appId:}")
    String appId;

    @Value("${WeixinPayService.partnerId:}")
    String mch_id;

    @Value("${WeixinPayService.partnerKey:}")
    String key;

    @Value("${WeixinPayService.notifyUrl:}")
    String payNotifyUrl;

    String refundNotifyUrl;

    @Value("${WeixinPayService.certPath:}")
    String certPath;

    @Value("${WeixinPayService.certPassword:}")
    String certPassword;

    RestTemplate restTemplate = null;

    @PostConstruct
    private void init() {
        HttpClient httpClient = getHttpClient();
        HttpComponentsClientHttpRequestFactory requestFactory = httpClient == null ?
                new HttpComponentsClientHttpRequestFactory() : new HttpComponentsClientHttpRequestFactory(httpClient);

        restTemplate = new RestTemplate(requestFactory);
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof SourceHttpMessageConverter) {
                ArrayList support = new ArrayList<MediaType>();
                support.add(new MediaType("text", "plain"));
                support.add(new MediaType("text", "html"));
                ((SourceHttpMessageConverter) converter).setSupportedMediaTypes(support);
                break;
            }
        }
    }

    //使用微信商户证书创建SSL连接
    private HttpClient getHttpClient() {
        try (InputStream inputStream = getClass().getResourceAsStream(certPath)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(inputStream, certPassword.toCharArray());
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, certPassword.toCharArray()).build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,
					new String[]{"TLSv1"}, null, new DefaultHostnameVerifier());
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            log.error("无法加载微信商户证书", e);
        }

        return null;
    }

    private static String getNonceStr() {
		return RandomStringUtils.randomAlphabetic(10);
    }

    private static String getTimeStamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    private static String sign(SortedMap<String, String> signParams, String key) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : signParams.entrySet())
            if (!StringUtils.isEmpty(entry.getValue()))
                sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        sb.append("key=" + key);
        log.debug("签名前：" + sb);
        return DigestUtils.md5Hex(getContentBytes(sb.toString(), CHARSET)).toUpperCase();
    }

    private void checkData(Map<String, String> data) {
        String return_code = data.get("return_code");
        String result_code = data.get("result_code");
        String return_msg = data.get("return_msg");
        String err_code_des = data.get("err_code_des");
        String err_code = data.get("err_code");
        if (!"SUCCESS".equals(result_code))
            throw new IllegalStateException(err_code + "," + err_code_des);
        if (!"SUCCESS".equals(return_code))
            throw new IllegalStateException("支付系统故障：" + return_msg);
    }


    private SortedMap<String, String> unpackData(DOMSource domSource) {
        SortedMap<String, String> data = new TreeMap<String, String>();
        String expectedSign = null;
        NodeList childNodes = ((Document)domSource.getNode()).getDocumentElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                Element element = (Element) node;
                String key = element.getTagName();
                String value = element.getTextContent();
                if (!TAG_SIGN.equals(key)) {
                    data.put(key, value);
                } else {
                    expectedSign = value;
                }
            }
        }
        if (StringUtils.isEmpty(expectedSign))
            throw new IllegalArgumentException("没有sign元素！");
        String mySign = sign(data, key);
        if (!expectedSign.equals(mySign))
            throw new IllegalArgumentException("数据签名错误！");

        return data;
    }


    private DOMSource packData(SortedMap<String, String> data) {
        String sign = sign(data, key);
        data.put(TAG_SIGN, sign);
        Document responseDoc = XmlUtils.newDocument();
        Element responseEl = responseDoc.createElement("xml");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!StringUtils.isEmpty(entry.getValue()))
                XmlUtils.addElement(responseEl, entry.getKey(), entry.getValue());
        }
        responseDoc.appendChild(responseEl);
        return new DOMSource(responseDoc);
    }


    private SortedMap<String, String> post(String url, SortedMap<String, String> params) {
        DOMSource response = restTemplate.postForObject(url, packData(params), DOMSource.class);
        return unpackData(response);
    }



//    public Map<String, String> getJsPayRequest(Order order) {
//        if (!order.isPayable())
//            throw new IllegalStateException("订单处于不可支付状态！");
//
//        String openId = getOpenId(order.getUserId());
//        if (openId == null)
//            throw new IllegalStateException("只用绑定了微信号的用户才能发红包");
//        Element rootEl = unifiedOrder(order, "JSAPI", openId);
//        String prepay_id = XmlUtils.firstChildElementText(rootEl, "prepay_id");
//        SortedMap<String, String> params = new TreeMap<String, String>();
//        params.put("nonceStr", getNonceStr());
//        params.put("timeStamp", getTimeStamp());
//        params.put("appId", appId);
//        params.put("package", "prepay_id=" + prepay_id);
//        params.put("signType", "MD5");
//        params.put("paySign", sign(params));
//        return params;
//    }
//
//
//    /**
//     * 扫码支付模式二获得url
//     *
//     * @param order
//     * @return
//     */
//    public String getNativePayUrl2(Order order) {
//        if (!order.isPayable())
//            throw new IllegalStateException("订单处于不可支付状态！");
//
//        Element rootEl = unifiedOrder(order, "NATIVE", null);
//        String code_url = XmlUtils.firstChildElementText(rootEl, "code_url");
//        return code_url;
//    }

    /**
     * 统一下单
     * @return
     */
    public Map<String, String> unifiedOrder(String out_trade_no, String trade_type, String openId, String product_id, String body, BigDecimal total_fee, LocalDateTime time_start, LocalDateTime time_expire) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", appId);
        params.put("mch_id", mch_id);
        params.put("device_info", "website");
        params.put("nonce_str", getNonceStr());
        params.put("body", body);
//		params.put("detail", detail);
//		params.put("attach", trade_type);
        params.put("out_trade_no", out_trade_no);
        params.put("fee_type", "CNY");
        params.put("total_fee", total_fee.toString());
        params.put("spbill_create_ip", "0.0.0.0");
        params.put("time_start", DATE_FORMAT.format(time_start));
        params.put("time_expire", DATE_FORMAT.format(time_expire));
        params.put("goods_tag", "WXG");
        params.put("notify_url", payNotifyUrl);
        params.put("trade_type", trade_type);
        params.put("product_id", product_id);
        if (openId != null)
            params.put("openid", openId);
        return post(URL_UNIFIED_ORDER, params);
    }

    public Map<String, String> orderQuery(String transaction_id, String out_trade_no) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", appId);
        params.put("mch_id", mch_id);
        params.put("transaction_id", transaction_id);
        params.put("out_trade_no", out_trade_no);
        params.put("nonce_str", getNonceStr());
        return post(URL_ORDER_QUERY, params);
    }

    public Map<String, String> refundQuery(String transaction_id, String out_trade_no, String out_refund_no, String refund_id) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", appId);
        params.put("mch_id", mch_id);
        params.put("transaction_id", transaction_id);
        params.put("out_trade_no", out_trade_no);
        params.put("out_refund_no", out_refund_no);
        params.put("refund_id", refund_id);
        params.put("nonce_str", getNonceStr());
        return post(URL_REFUND_QUERY, params);
    }

    public Map<String, String> refund(String transaction_id, String out_trade_no, String out_refund_no, BigDecimal total_fee, BigDecimal refund_fee) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", appId);
        params.put("mch_id", mch_id);
        params.put("transaction_id", transaction_id);
        params.put("out_trade_no", out_trade_no);
        params.put("out_refund_no", out_refund_no);
        params.put("total_fee", total_fee.toString());
        params.put("refund_fee", refund_fee.toString());
        params.put("notify_url", refundNotifyUrl);
        params.put("nonce_str", getNonceStr());
        return post(URL_REFUND, params);
    }


    public Map<String, String> sendRedPack(String openId, long total_amount, long min_value, long max_value, long total_num, String nick_name, String send_name, String wishing, String act_name, String remark, String logo_imgurl, String share_content, String share_url, String share_imgurl) {
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("nonce_str", getNonceStr());
        params.put("mch_billno", "rd_"  + UUID.randomUUID().toString());
        params.put("wxappid", appId);
        params.put("mch_id", mch_id);
        params.put("nick_name", nick_name);
        params.put("send_name", send_name);
        params.put("re_openid", openId);
        params.put("total_amount", Long.toString(total_amount));
        params.put("min_value", Long.toString(min_value));
        params.put("max_value", Long.toString(max_value));
        params.put("total_num", Long.toString(total_num));
        params.put("wishing", wishing);
        params.put("client_ip", "1.1.1.1");
        params.put("act_name", act_name);
        params.put("remark", remark);
        params.put("logo_imgurl", logo_imgurl);
        params.put("share_content", share_content);
        params.put("share_url", share_url);
        params.put("share_imgurl", share_imgurl);
        return post(URL_SEND_RED_PACK, params);
    }

    /**
     * 处理微信支付支付结果回调
     * @param dataSource
     * @return
     */
    public DOMSource onPayNotify(DOMSource dataSource) {
        SortedMap<String, String> ret = new TreeMap<String, String>();
        try {
            Map<String, String> data = unpackData(dataSource);
            checkData(data);
            weixinPayHandler.handleNotification(data);
            ret.put("return_code", "SUCCESS");
            ret.put("return_msg", "OK");
        } catch (Exception e) {
            log.error("Fail to response pay!", e);
            ret.put("return_code", "FAIL");
            ret.put("return_msg", e.getMessage());
        }
        return packData(ret);
    }
}
