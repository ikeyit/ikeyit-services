package com.ikeyit.mqhelper;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.rocketmq.spring.support.RocketMQMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.List;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class MqConfig {

    @Bean
    public RocketMQMessageConverter createRocketMQMessageConverter() {
        RocketMQMessageConverter rocketMQMessageConverter =  new RocketMQMessageConverter();
        List<MessageConverter> converters = ((CompositeMessageConverter) rocketMQMessageConverter.getMessageConverter()).
                getConverters();
        for (MessageConverter converter : converters) {
            if (converter instanceof MappingJackson2MessageConverter) {
                MappingJackson2MessageConverter jackson2MessageConverter = (MappingJackson2MessageConverter) converter;
                ObjectMapper objectMapper = new ObjectMapper();
                JavaTimeModule javaTimeModule = new JavaTimeModule();
                objectMapper.registerModule(javaTimeModule);
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                jackson2MessageConverter.setObjectMapper(objectMapper);
                break;
            }
        }
        return rocketMQMessageConverter;
    }


    @Bean
    public LockProvider lockProvider(RedisTemplate redisTemplate) {
        return new RedisLockProvider(redisTemplate.getConnectionFactory());

    }
}
