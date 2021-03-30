package com.ikeyit.security.jwt;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RedisRefreshTokenRevoker implements RefreshTokenRevoker {

    private static final String KEY_PREFIX_USER = "refresh_token_sub_blacklist:";

    private static final String KEY_PREFIX_TOKEN = "refresh_token_blacklist:";

    StringRedisTemplate redisTemplate;

    public RedisRefreshTokenRevoker(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean isRevoked(RefreshToken refreshToken) {
        String blockBeforeVal = redisTemplate.opsForValue().get(KEY_PREFIX_USER + refreshToken.getSubject());
        if (blockBeforeVal != null) {
            LocalDateTime blockBefore = LocalDateTime.parse(blockBeforeVal, DateTimeFormatter.ISO_DATE_TIME);
            if (refreshToken.getCreateTime().isBefore(blockBefore))
                return true;
        }

        String token = redisTemplate.opsForValue().get(KEY_PREFIX_TOKEN + refreshToken.getToken());
        if (token != null) {
            return true;
        }

        return false;
    }

    @Override
    public void revoke(RefreshToken refreshToken) {
        Duration duration = Duration.between(LocalDateTime.now(), refreshToken.getExpireTime());
        if (duration.isZero() || duration.isNegative())
            return;
        redisTemplate.opsForValue().set(
                KEY_PREFIX_TOKEN +  refreshToken.getToken(),
                "1",
                duration
                );
    }

    @Override
    public void revoke(String subject, LocalDateTime before, Duration duration) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX_USER +  subject,
                before.format(DateTimeFormatter.ISO_DATE_TIME),
                duration
        );
    }

}
