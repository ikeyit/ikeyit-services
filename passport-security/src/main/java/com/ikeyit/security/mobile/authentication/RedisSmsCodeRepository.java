package com.ikeyit.security.mobile.authentication;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

public class RedisSmsCodeRepository implements SmsCodeRepository {

    private static String DEFAULT_KEY_PREFIX = "AUTH_";

    StringRedisTemplate redisTemplate;

    String keyPrefix ;

    public RedisSmsCodeRepository(StringRedisTemplate redisTemplate) {
       this(redisTemplate, DEFAULT_KEY_PREFIX);
    }

    public RedisSmsCodeRepository(StringRedisTemplate redisTemplate, String keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
    }

    @Override
    public void save(SmsCode code) {
        redisTemplate.boundValueOps(keyPrefix + code.getMobile()).set(code.getCode() + " " + code.getExpireTime().toString(), Duration.between(LocalDateTime.now(), code.getExpireTime()));
    }

    @Override
    public SmsCode getByMobile(String mobile) {
        String data = (String) redisTemplate.boundValueOps(keyPrefix + mobile).get();
        if (data == null)
            return null;
        String[] fields = data.split(" ");

        SmsCode smsCode = new SmsCode(mobile, fields[0], null, LocalDateTime.parse(fields[1]));
        return smsCode;
    }
}
