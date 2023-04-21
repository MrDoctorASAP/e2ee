package com.e2ee.api.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/gs-guide-websocket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT,
                        SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT).permitAll()
             .anyMessage().permitAll()
             .simpDestMatchers("/**").permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
