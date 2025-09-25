package com.tripPlanner.project.config;

import com.tripPlanner.project.component.JwtHandshakeInterceptor;
import com.tripPlanner.project.component.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class ChattingConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url = ws://localhost:8080/ws, 프로토콜이 http가 아님
        registry.addEndpoint("/ws-stomp") // 연결된 엔드포인트
                .setAllowedOrigins("*")
                .addInterceptors(jwtHandshakeInterceptor);   // Handshake에서 쿠키→인증 심기

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 발행(송신)하는 엔드 포인트, 클라이언트가 서버로 보낼 메시지 prefix
        registry.setApplicationDestinationPrefixes("/pub");

        // 메시지 구독(수신)하는 요청 엔드 포인트
        registry.enableSimpleBroker("/sub", "/queue");
        // → 일반 broadcast는 /sub, 1:1 유저별 큐는 /queue

        // 3) 유저 큐의 prefix
        registry.setUserDestinationPrefix("/user");
        // → convertAndSendToUser()가 내부적으로 /user/{session}/queue/... 로 매핑됨

    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthChannelInterceptor); // CONNECT에서 Principal 세팅
    }



}
