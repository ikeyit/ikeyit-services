package com.ikeyit.product.config;


import com.ikeyit.common.exception.RestErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public RestErrorController globalErrorController(ErrorAttributes errorAttributes, MessageSource messageSource) {
        return new RestErrorController(errorAttributes, messageSource);
    }
}
