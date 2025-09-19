package com.tripPlanner.project.component;

import com.tripPlanner.project.dto.CustomUserDetails;
import com.tripPlanner.project.dto.ws.TokenUserInfo;
import com.tripPlanner.project.dto.ws.WsUserPrincipal;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler webSocketHandler, Map<String, Object> attributes) {
        if(!(request instanceof ServletServerHttpRequest sr)) {
            return  true;
        }

        Cookie[] cookies = sr.getServletRequest().getCookies();
        if(cookies == null) {
            log.warn("[WS] cookies is null");
            return true; // 필요시 false 가능
        }

        String token = null;
        for(Cookie c : cookies) {
            if ("Authorization".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }

        if (token == null || token.isBlank()) {
            log.warn("[WS] accessToken cookie not found");
            return true; // 필요시 false로 차단
        }

        System.out.println("token값 확인"+token);

        try {
            // 1. 토큰에서 username/socialType 추출 (+ 만료/서명 검증은 JWTUtil 내부)
            TokenUserInfo tokenUserInfo = jwtUtil.getUserInfoOrThrow(token);
            String socialType = tokenUserInfo.getSocialType();
            String name = tokenUserInfo.getUsername();

            // 2. DB에서 사용자 조회
            UserEntity user = new UserEntity();
            if(socialType.equals("localUser")){
                user = userRepository.findByUsernameAndSocialType(name, "localUser");

            }else {
                user = userRepository.findByNameAndSocialType(name, socialType);
            }
/*
            // 3. Authentication 생성 (권한은 필요시 채움)
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    // Principal에 userId 포함되도록 커스텀 객체 or UserEntity 저장
                    new CustomUserDetails(user),
                    null,
                    new CustomUserDetails(user).getAuthorities() // 권한
            );
*/
            Long userId = user.getId();
            // 4. WebSocket 세션 속성에 저장 -> CONNECT 에서 끄집어냄
            attributes.put("userId", userId);

            log.debug("[WS] auth stored in WS session attrs, userId={}", user.getId());

        } catch (Exception e) {
            log.warn("[WS] handshake auth failed: {}", e.getMessage());
            // 여기서 false로 리턴하면 업그레이드 자체를 막습니다.
            // 우선 true로 두고 익명 연결을 허용할지, false로 차단할지 정책에 맞게 결정하세요.
            // return false;
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }


}
