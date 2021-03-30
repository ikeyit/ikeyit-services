package com.ikeyit.trade.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikeyit.common.exception.ErrorResponse;
import com.ikeyit.common.exception.ServiceException;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Configuration
public class FeignConfig {

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    ErrorDecoder errorDecoder() {
        return new ErrorDecoder() {

            @Override
            public Exception decode(String methodKey, Response response) {
                try {

                    ErrorResponse errorResponse = objectMapper.readValue(response.body().asReader(StandardCharsets.UTF_8), ErrorResponse.class);
                    return new ServiceException(errorResponse.getErrCode(), errorResponse.getErrMsg(), response.status());
                } catch (IOException e) {

                    return new IllegalStateException("IO错误", e);
                }
            }
        };
    }


    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String authorization = request.getHeader("Authorization");
            if (authorization != null)
                requestTemplate.header("Authorization", authorization);
        };
    }

}
