package com.ikeyit.passport.config;


import com.ikeyit.passport.repository.DbWeixinClientRepository;
import com.ikeyit.passport.resource.AuthenticationService;
import com.ikeyit.passport.resource.JwtConfigurerCustomizer;
import com.ikeyit.passport.resource.impl.AuthenticationServiceImpl;
import com.ikeyit.passport.service.UserPrincipal;
import com.ikeyit.passport.service.UserPrincipalService;
import com.ikeyit.security.jwt.JwtService;
import com.ikeyit.security.jwt.RedisRefreshTokenRevoker;
import com.ikeyit.security.jwt.RefreshTokenRevoker;
import com.ikeyit.security.jwt.config.JwtConfigurer;
import com.ikeyit.security.mobile.authentication.SmsCodeService;
import com.ikeyit.security.mobile.config.SmsCodeAuthenticationConfigurer;
import com.ikeyit.security.social.config.WeixinAuthenticationConfigurer;
import com.ikeyit.security.social.weixin.PropertiesWeixinClientRepository;
import com.ikeyit.security.social.weixin.WeixinClientRepository;
import com.ikeyit.security.verification.DefaultVerificationCodeService;
import com.ikeyit.security.verification.RedisVerificationCodeRepository;
import com.ikeyit.security.verification.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;
import java.util.HashMap;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    SmsCodeService smsCodeService;

    UserPrincipalService userPrincipalService;

    @Autowired
    public SecurityConfig(SmsCodeService smsCodeService, UserPrincipalService userPrincipalService) {
        this.smsCodeService = smsCodeService;
        this.userPrincipalService = userPrincipalService;
    }


    @Bean
    public WeixinClientRepository weixinClientRepository() {
        return new PropertiesWeixinClientRepository();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //通过用户名登录
        auth.userDetailsService(userPrincipalService).passwordEncoder(passwordEncoder());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/error");
    }


    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${passport.jwt.keystore.file}")
    private String jwtKeystoreFile;

    @Value("${passport.jwt.keystore.password}")
    private String jwtKeystorePassword;

    @Value("${passport.jwt.key.alias}")
    private String jwtKeyAlias;

    @Value("${passport.jwt.key.password}")
    private String jwtKeyPassword;

    @Value("${passport.jwt.access-token-lifetime:0}")
    private long jwtAccessTokenLifetime;

    @Value("${passport.jwt.refresh-token-lifetime:0}")
    private long jwtRefreshTokenLifetime;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    DbWeixinClientRepository dbWeixinClientRepository;


    @Bean
    public RefreshTokenRevoker redisRefreshTokenRevoker() {
        return new RedisRefreshTokenRevoker(redisTemplate);
    }

    @Bean
    public VerificationCodeService verificationCodeService() {
        RedisVerificationCodeRepository redisVerificationCodeRepository = new RedisVerificationCodeRepository(redisTemplate);
        return new DefaultVerificationCodeService(redisVerificationCodeRepository);
    }

    @Bean
    public JwtService jwtService() {
        JwtService jwtService = new JwtService() {
            @Override
            public void buildResponseEntity(UserDetails principal, HashMap<String, Object> responseEntity) {
                UserPrincipal userPrincipal = (UserPrincipal) principal;
                responseEntity.put("id", userPrincipal.getUserId());
                responseEntity.put("name", userPrincipal.getLoginName());
                responseEntity.put("nick", userPrincipal.getNick());
                responseEntity.put("avatar", userPrincipal.getAvatar());
            }
        };

        if (jwtKeystoreFile != null) {
            KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(
                    resourceLoader.getResource(jwtKeystoreFile),
                    jwtKeystorePassword == null ? null : jwtKeystorePassword.toCharArray());
            KeyPair keyPair = ksFactory.getKeyPair(jwtKeyAlias, jwtKeyPassword == null ? null : jwtKeyPassword.toCharArray());
            jwtService.setKeyPair(keyPair);
        }

        if (jwtAccessTokenLifetime > 0)
            jwtService.setAccessTokenLifetime(jwtAccessTokenLifetime);
        if (jwtRefreshTokenLifetime > 0)
            jwtService.setRefreshTokenLifetime(jwtRefreshTokenLifetime);
        jwtService.setRefreshTokenRevoker(redisRefreshTokenRevoker());
        return jwtService;
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //允许浏览器客户端跨域调用，
        http.cors().configurationSource(request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
//                corsConfiguration.addAllowedMethod(HttpMethod.PUT);
//                corsConfiguration.addAllowedMethod(HttpMethod.PATCH);
//                corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);
//                corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
            corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
            corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
//                corsConfiguration.setAllowCredentials(true);
//                corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
            return corsConfiguration;
        }).and()
            //禁用无用的防跨站请求伪造攻击
            .csrf().disable()
            //禁用basic认证
            .httpBasic().disable()
            //禁用默认的注销
            .logout().disable()
            //禁用匿名登录
            .anonymous().disable()
            //禁用remember me
            .rememberMe().disable()
            //禁用session,跨请求将无法获得认证信息，在restful中不需要
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            //允许浏览器客服端ajax跨域读取Authorization header
//            .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
//                new Header("Access-Control-Expose-Headers","Authorization")))).and()

            .formLogin()
                .loginProcessingUrl("/auth/login")
//                .successHandler(authenticationSuccessHandler())
//                .failureHandler(authenticationFailureHandler())
            .and()
            .apply(new JwtConfigurer<HttpSecurity>()
                    .jwksUrl("/auth/jwks")
                    .refreshTokenUrl("/auth/refresh_token")
                    .refreshTokenUserService(userPrincipalService)
                    .jwtService(jwtService())).and()
            //添加短信验证码认证
            .apply(new SmsCodeAuthenticationConfigurer<HttpSecurity>()
                    .sendSmsUrl("/auth/smscode/code")
                    .loginUrl("/auth/smscode")
                    .smsCodeService(smsCodeService)
                    .mobileUserService(userPrincipalService)).and()
            //添加微信认证
            .apply(new WeixinAuthenticationConfigurer<HttpSecurity>()
                    .appSocialUserService(userPrincipalService)
                    .weixinClientRepository(dbWeixinClientRepository)
                    .loginUrl("/auth/weixin")).and()
            .authorizeRequests()
                .anyRequest().authenticated().and()
            .exceptionHandling()
                .accessDeniedHandler(this::accessDeniedHandler)
                .authenticationEntryPoint(this::authenticationEntryPoint).and()
            .oauth2ResourceServer()
                .bearerTokenResolver(bearerTokenResolver())
                .authenticationEntryPoint(this::authenticationEntryPoint)
                .jwt(JwtConfigurerCustomizer::customize);
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        //允许通过url query parameter传递token！ 初始化客户端初始化websocket会使用url query parameter来传递token来进行验证身份
        DefaultBearerTokenResolver bearerTokenResolver =  new DefaultBearerTokenResolver();
        bearerTokenResolver.setAllowUriQueryParameter(true);
        bearerTokenResolver.setAllowFormEncodedBodyParameter(true);
        return bearerTokenResolver;
    }

    private void accessDeniedHandler(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, exception.getMessage());
    }

    private void authenticationEntryPoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationServiceImpl();
    }
}