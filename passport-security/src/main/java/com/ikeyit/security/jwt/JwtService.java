package com.ikeyit.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class JwtService {


    RSAKey rsaKey;

    KeyPair keyPair;

    // 默认15分钟过期
    long accessTokenLifetime = 300;

    // 默认30天过期
    long refreshTokenLifetime = 30 * 86400;

    String keyId = "jwk";

    RefreshTokenRevoker refreshTokenRevoker;

    public JwtService() {
        this(null);
    }

    public JwtService(KeyPair keyPair) {
       setKeyPair(keyPair);
    }

    public void setKeyPair(KeyPair keyPair) {
        try {
            if (keyPair == null) {
                rsaKey = new RSAKeyGenerator(2048)
                        .keyID(keyId)
                        .keyUse(KeyUse.SIGNATURE)
                        .generate();
            } else {
                rsaKey = new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
                        .privateKey(keyPair.getPrivate())
                        .keyID(keyId)
                        .keyUse(KeyUse.SIGNATURE)
                        .build();
            }

        } catch (JOSEException e) {
            throw new IllegalStateException("JwtService init fail!", e);
        }
    }

    public void setRefreshTokenRevoker(RefreshTokenRevoker refreshTokenRevoker) {
        this.refreshTokenRevoker = refreshTokenRevoker;
    }

    public void setAccessTokenLifetime(long accessTokenLifetime) {
        this.accessTokenLifetime = accessTokenLifetime;
    }

    public void setRefreshTokenLifetime(long refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public JWKSet getJwkSet() {
        return new JWKSet(rsaKey).toPublicJWKSet();
    }

    /**
     * 生成
     * @param principal
     * @return
     * @throws JOSEException
     */
    public String createAccessToken(UserDetails principal)  {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        List<String> authorities = principal.getAuthorities().stream().map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        builder.subject(principal.getUsername())
                .claim("scp", authorities)
                .expirationTime(new Date(System.currentTimeMillis() + accessTokenLifetime * 1000));
        buildAccessTokenClaims(principal, builder);
        return generateToken(builder);
    }

    protected void buildAccessTokenClaims(UserDetails principal, JWTClaimsSet.Builder builder) {

    }

    public String createRefreshToken(UserDetails principal) {
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        builder.claim("rsub", principal.getUsername())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + refreshTokenLifetime * 1000));
        buildRefreshTokenClaims(principal, builder);
        return generateToken(builder);
    }

    protected void buildRefreshTokenClaims(UserDetails principal, JWTClaimsSet.Builder builder) {

    }

    protected String generateToken(JWTClaimsSet.Builder claimsSetBuilder) {
        try {
            JWSSigner signer = new RSASSASigner(rsaKey);
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build();
            JWTClaimsSet jwtClaimsSet  = claimsSetBuilder.build();
            SignedJWT signedJWT  = new SignedJWT(jwsHeader, jwtClaimsSet);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("创建JWT失败", e);
        }
    }

    protected JWTClaimsSet parseToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier jwsVerifier = new RSASSAVerifier(rsaKey);
            if(!signedJWT.verify(jwsVerifier))
                throw new BadCredentialsException("无效TOKEN, 签名不正确");
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new BadCredentialsException("无效TOKEN，格式不正确", e);
        }
    }

    public RefreshToken decodeRefreshToken(String token) {
        return decodeRefreshToken(token, true);
    }

    public RefreshToken decodeRefreshToken(String token, boolean autoClear) {
        JWTClaimsSet claims = parseToken(token);
        Date issueTime = claims.getIssueTime();
        Date expireTime = claims.getExpirationTime();
        if (issueTime == null || expireTime == null)
            throw new BadCredentialsException("无效TOKEN，缺少合法的颁布和过期时间！");
        String subject = null;
        try {
            subject = claims.getStringClaim("rsub");
        } catch (ParseException e) {
            throw new BadCredentialsException("无效TOKEN，缺少合法的主体");
        }
        if (subject == null)
            throw new BadCredentialsException("无效TOKEN，主体为空");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setSubject(subject);
        refreshToken.setCreateTime(issueTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        refreshToken.setExpireTime(expireTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        if (refreshToken.getExpireTime().isBefore(LocalDateTime.now()))
            throw new BadCredentialsException("无效TOKEN,已过期");
        if (refreshTokenRevoker != null && refreshTokenRevoker.isRevoked(refreshToken))
            throw new BadCredentialsException("无效TOKEN,已吊销");

        //自动清理，保证token只使用一次
        if (autoClear && refreshTokenRevoker != null)
            refreshTokenRevoker.revoke(refreshToken);
        return refreshToken;
    }



    public void buildResponseEntity(UserDetails principal, HashMap<String, Object> responseEntity)  {
        responseEntity.put("username", principal.getUsername());
        responseEntity.put("enabled", principal.isEnabled());
    }

}
