package com.ikeyit.security.mobile.authentication;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DefaultSmsCodeService implements SmsCodeService{
    private SmsCodeRepository smsCodeRepository;

    private SmsCodeSender smsCodeSender;

    private int codeSize = 4;

    private int codeLife = 120; //有效期2分钟

    private int resendInterval = 60; //60秒才能重发

    public DefaultSmsCodeService() {
        this.smsCodeRepository = new InMemeorySmsCodeRepository();
        this.smsCodeSender = new ConsoleSmsCodeSender();
    }

    public DefaultSmsCodeService(SmsCodeRepository smsCodeRepository, SmsCodeSender smsCodeSender) {
        this.smsCodeRepository = smsCodeRepository;
        this.smsCodeSender = smsCodeSender;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }

    @Override
    public void sendCode(String mobile) {
        if (!StringUtils.isNumeric(mobile))
            throw new SmsCodeException("手机号格式不正确");

        SmsCode smsCode = smsCodeRepository.getByMobile(mobile);
        LocalDateTime now = LocalDateTime.now();

        if (smsCode != null && now.isBefore(smsCode.getSendTime().plus(resendInterval, ChronoUnit.SECONDS)))
            throw new SmsCodeException("发送验证码太频繁了，应该间隔60秒");

        String code = RandomStringUtils.randomNumeric(codeSize);
        smsCode = new SmsCode(mobile, code, LocalDateTime.now().plus(codeLife, ChronoUnit.SECONDS));
        if (!smsCodeSender.sendSms(mobile, code))
            throw new SmsCodeException("发送验证码失败");

        //成功发送
        smsCodeRepository.save(smsCode);
    }

    /**
     *
     * @param mobile
     * @return null 假如没有发code或者过期了返回null
     */
    @Override
    public SmsCode getCode(String mobile) {
        return this.smsCodeRepository.getByMobile(mobile);
    }

    @Override
    public void validate(String mobile, String code) throws SmsCodeException {
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code))
            throw new SmsCodeException("手机号或验证码不能为空");

        SmsCode smsCode = getCode(mobile);
        if (smsCode == null)
            throw new SmsCodeException("未发送验证码或验证码已经过期，请重新发送！");

        if (!smsCode.getCode().equals(code))
            throw new SmsCodeException("验证码输入错误！");

        if (smsCode.getExpireTime() != null && smsCode.getExpireTime().isBefore(LocalDateTime.now()))
            throw new SmsCodeException("验证码已经过期，请重新发送！");
    }
}
