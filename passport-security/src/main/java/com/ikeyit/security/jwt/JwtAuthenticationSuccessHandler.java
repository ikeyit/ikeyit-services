package com.ikeyit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;


/**
 * 当认证成功后，返回TOKEN和用户信息给客户端
 */
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private JwtService jwtService;


    public JwtAuthenticationSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal =  authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            String token = jwtService.createAccessToken(userDetails);
            String refreshToken = jwtService.createRefreshToken(userDetails);
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            response.addHeader("Refresh-Token", refreshToken);
            response.addHeader("Access-Control-Expose-Headers","Authorization, Refresh-Token");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            HashMap<String, Object> responseEntity = new HashMap<>();
            jwtService.buildResponseEntity(userDetails, responseEntity);
            objectMapper.writeValue(response.getWriter(), responseEntity);
        } else {
            throw new IllegalStateException("Principal必须为UserDetails子类");
        }
    }

}
