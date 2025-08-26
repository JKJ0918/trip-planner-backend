package com.tripPlanner.project.controller.notification;

import com.tripPlanner.project.component.NotificationStreamer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
public class NotificationSseController {

    private final NotificationStreamer streamer;

    public NotificationSseController(NotificationStreamer streamer) {
        this.streamer = streamer;
    }

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public SseEmitter stream(HttpServletRequest req) {
        Long userId = getUserId(req); // ↓ 아래 유틸 참조
        return streamer.subscribe(userId);
    }

    private Long getUserId(HttpServletRequest req) {
        // JWT에서 꺼내는 당신의 기존 유틸을 연결하세요.
        // 예) (Long) req.getAttribute("userId") or JwtUtil.extractUserId(req)
        Object v = req.getAttribute("userId");
        if (v instanceof Long l) return l;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }

}
