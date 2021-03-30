package com.ikeyit.passport.resource;

public interface AuthenticationService {
    Long getCurrentUserId();
    boolean hasAuthority(String authority);
    void requireAuthority(String authority);
}
