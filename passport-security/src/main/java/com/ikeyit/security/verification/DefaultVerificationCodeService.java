package com.ikeyit.security.verification;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DefaultVerificationCodeService implements VerificationCodeService {

    private VerificationCodeRepository verificationCodeRepository;

    private int codeSize = 4;

    private int codeLife = 120; //有效期2分钟

    private int resendInterval = 60; //60秒才能重发

    public DefaultVerificationCodeService(VerificationCodeRepository verificationCodeRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
    }

    public int getCodeSize() {
        return codeSize;
    }

    public void setCodeSize(int codeSize) {
        this.codeSize = codeSize;
    }

    public int getCodeLife() {
        return codeLife;
    }

    public void setCodeLife(int codeLife) {
        this.codeLife = codeLife;
    }

    public int getResendInterval() {
        return resendInterval;
    }

    public void setResendInterval(int resendInterval) {
        this.resendInterval = resendInterval;
    }

    /**
     * 生成验证码，并保存在存储中
     * @param target
     * @return
     */
    @Override
    public VerificationCode generateCode(String target) {
        VerificationCode verificationCode = verificationCodeRepository.getByTarget(target);
        LocalDateTime now = LocalDateTime.now();
        if (verificationCode != null && now.isBefore(verificationCode.getSendTime().plus(resendInterval, ChronoUnit.SECONDS)))
            throw new VerificationCodeException("发送验证码太频繁了，应该间隔60秒");
        String code = RandomStringUtils.randomNumeric(codeSize);
        verificationCode = new VerificationCode();
        verificationCode.setTarget(target);
        verificationCode.setCode(code);
        verificationCode.setSendTime(now);
        verificationCode.setExpireTime(now.plus(codeLife, ChronoUnit.SECONDS));
        //成功发送
        verificationCodeRepository.save(verificationCode);
        return verificationCode;
    }

    /**
     * 判断验证码是否正确，如果正确则从存储中删除，并返回验证码,否则抛出异常
     * @param target
     * @param code
     * @throws VerificationCodeException
     */
    @Override
    public VerificationCode validate(String target, String code) throws VerificationCodeException {
        if (StringUtils.isEmpty(code))
            throw new VerificationCodeException("验证码不能为空");

        VerificationCode verificationCode = verificationCodeRepository.getByTarget(target);

        if (verificationCode == null)
            throw new VerificationCodeException("未发送验证码或验证码已经过期，请重新发送！");

        if (!verificationCode.getCode().equals(code))
            throw new VerificationCodeException("验证码输入错误！");

        if (verificationCode.getExpireTime() != null && verificationCode.getExpireTime().isBefore(LocalDateTime.now()))
            throw new VerificationCodeException("验证码已经过期，请重新发送！");

        verificationCodeRepository.deleteByTarget(target);

        return verificationCode;
    }

}
