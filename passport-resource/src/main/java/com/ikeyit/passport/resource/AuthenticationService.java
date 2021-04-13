package com.ikeyit.passport.resource;

import com.ikeyit.common.exception.BusinessException;

public interface AuthenticationService {
    Long getCurrentUserId();
    Long pollCurrentUserId();
    boolean hasAuthority(String authority);
    void requireAuthority(String authority) throws BusinessException;
}
