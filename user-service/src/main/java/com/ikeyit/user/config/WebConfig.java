package com.ikeyit.user.config;


import com.ikeyit.common.exception.RestErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;

//穷！合并服务！独立服务时需取消注释
//@Configuration
public class WebConfig {

    @Bean
    public RestErrorController globalErrorController(ErrorAttributes errorAttributes, MessageSource messageSource) {
        return new RestErrorController(errorAttributes, messageSource);
    }
}
