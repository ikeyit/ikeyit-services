package com.ikeyit.message.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/message_center").setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {

        });
    }

    @Bean
    public ApplicationListener sessionConnectedEventListener() {

        return new ApplicationListener<SessionConnectedEvent>() {
            @Override
            public void onApplicationEvent(SessionConnectedEvent event) {
//                System.out.println("Connected user: " + event.getUser());
            }
        };
    }



    @Bean
    public ApplicationListener sessionDisconnectEventListener() {

        return new ApplicationListener<SessionDisconnectEvent>() {
            @Override
            public void onApplicationEvent(SessionDisconnectEvent event) {
//                System.out.println("Disconnected user: " + event.getUser());
            }
        };
    }


}