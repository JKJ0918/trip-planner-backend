package com.tripPlanner.project.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    // 구독 가드 전용으로 사용예정 일단 보류 2025 09 20
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        // CONNECT 시점에 세션 attributes에서 userId를 꺼내 Principal로 주입
        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            Map<String, Object> attrs = acc.getSessionAttributes();
            if (attrs != null && attrs.get("userId") != null) {
                String name = String.valueOf(attrs.get("userId"));
                Authentication auth = new UsernamePasswordAuthenticationToken(name, null, List.of());
                acc.setUser(auth);
            }
        }
        return message;
    }
}
