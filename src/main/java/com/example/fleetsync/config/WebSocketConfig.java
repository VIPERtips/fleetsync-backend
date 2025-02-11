package com.example.fleetsync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       
        registry.addEndpoint("/ws").withSockJS(); 
        System.out.println("WebSocket endpoint registered at /ws");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        
        config.enableSimpleBroker("/topic", "/queue");  
        config.setApplicationDestinationPrefixes("/app");
    }

   
}
