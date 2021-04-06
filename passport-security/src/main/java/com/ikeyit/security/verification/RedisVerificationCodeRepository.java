package com.ikeyit.security.verification;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

public class RedisVerificationCodeRepository implements VerificationCodeRepository {

    private static String DEFAULT_KEY_PREFIX = "VC_";

    StringRedisTemplate redisTemplate;

    String keyPrefix ;

    public RedisVerificationCodeRepository(StringRedisTemplate redisTemplate) {
       this(redisTemplate, DEFAULT_KEY_PREFIX);
    }

    public RedisVerificationCodeRepository(StringRedisTemplate redisTemplate, String keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void save(VerificationCode verificationCode) {
        redisTemplate.boundValueOps(keyPrefix + verificationCode.getTarget())
                .set(verificationCode.getCode() + " " + verificationCode.getExpireTime().toString() + " " + verificationCode.getSendTime().toString(),
                Duration.between(LocalDateTime.now(), verificationCode.getExpireTime()));
    }

    @Override
    public VerificationCode getByTarget(String target) {
        String data = (String) redisTemplate.boundValueOps(keyPrefix + target).get();
        if (data == null)
            return null;
        String[] fields = data.split(" ");
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setTarget(target);
        verificationCode.setCode(fields[0]);
        verificationCode.setExpireTime(LocalDateTime.parse(fields[1]));
        verificationCode.setSendTime(LocalDateTime.parse(fields[2]));
        return verificationCode;
    }

    @Override
    public void deleteByTarget(String target) {
        redisTemplate.delete(keyPrefix + target);
    }
}
