package com.ikeyit.passport.service;

import com.ikeyit.security.mobile.authentication.ConsoleSmsCodeSender;
import com.ikeyit.security.mobile.authentication.DefaultSmsCodeService;
import com.ikeyit.security.mobile.authentication.RedisSmsCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSmsCodeService extends DefaultSmsCodeService {

    @Autowired
    public RedisSmsCodeService(StringRedisTemplate stringRedisTemplate) {
        super(new RedisSmsCodeRepository(stringRedisTemplate), new ConsoleSmsCodeSender());
    }

}
