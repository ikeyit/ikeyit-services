package com.ikeyit.passport.resource;

import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 提取JWT中的权限信息
 */
public class JwtConfigurerCustomizer {
    public static final String AUTH_CLAIM_NAME = "scp";
    public static void customize(OAuth2ResourceServerConfigurer.JwtConfigurer jwtConfigurer) {
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object authorities = jwt.getClaim(AUTH_CLAIM_NAME);
            if (authorities == null)
                return Collections.emptyList();

            if (authorities instanceof Collection) {
                return ((Collection<String>) authorities).stream()
                        .map(item -> new SimpleGrantedAuthority(item))
                        .collect(Collectors.toSet());
            }

            throw new IllegalArgumentException("bad jwt!");
        });

        jwtConfigurer.jwtAuthenticationConverter(authenticationConverter);

    }
}
