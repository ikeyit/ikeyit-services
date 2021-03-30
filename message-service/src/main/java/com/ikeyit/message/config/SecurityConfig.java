package com.ikeyit.message.config;


import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//穷！合并服务！独立服务时需取消注释
//@Configuration
//@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/error");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            //允许浏览器客户端跨域调用，
//            .cors().configurationSource(request -> {
//                CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
//                corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
//                return corsConfiguration;
//            }).and()
//            //禁用无用的防跨站请求伪造攻击
//            .csrf().disable()
//            //禁用basic认证
//            .httpBasic().disable()
//            //禁用form认证
//            .formLogin().disable()
//            //禁用默认的注销
//            .logout().disable()
//            //禁用匿名登录
//            .anonymous().disable()
//            //禁用remember me
//            .rememberMe().disable()
//            //禁用session,跨请求将无法获得认证信息，在restful中不需要
//            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//            .authorizeRequests()
//            .anyRequest().authenticated().and()
//            .exceptionHandling()
//                .accessDeniedHandler(this::accessDeniedHandler)
//                .authenticationEntryPoint(this::authenticationEntryPoint).and()
//            .oauth2ResourceServer()
//                .bearerTokenResolver(bearerTokenResolver())
//                .authenticationEntryPoint(this::authenticationEntryPoint)
//                .jwt();
////            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
//    }
//
//    @Bean
//    public BearerTokenResolver bearerTokenResolver() {
//        DefaultBearerTokenResolver bearerTokenResolver =  new DefaultBearerTokenResolver();
//        bearerTokenResolver.setAllowUriQueryParameter(true);
//        bearerTokenResolver.setAllowFormEncodedBodyParameter(true);
//        return bearerTokenResolver;
//
//    }
//
//    private void accessDeniedHandler(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
//        response.sendError(HttpServletResponse.SC_FORBIDDEN,  exception.getMessage());
//    }
//    private void authenticationEntryPoint(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
//    }
//
//    @Bean
//    public AuthenticationService authenticationService() {
//        return new AuthenticationServiceImpl();
//    }
}