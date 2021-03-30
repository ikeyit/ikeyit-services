package com.ikeyit.security.jwt;

import java.time.Duration;
import java.time.LocalDateTime;

public interface RefreshTokenRevoker {

    boolean isRevoked(RefreshToken refreshToken);

    void revoke(RefreshToken refreshToken);

    void revoke(String userName, LocalDateTime before, Duration duration);
}
