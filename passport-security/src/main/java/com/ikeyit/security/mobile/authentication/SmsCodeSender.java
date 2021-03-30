package com.ikeyit.security.mobile.authentication;

/**
 * 调用第三方接口发送登录验证码
 * 正规实现是应该异步发送验证，异步线程或者消息队列
 *
 */
public interface SmsCodeSender {

    boolean sendSms(String mobile, String code);
}
