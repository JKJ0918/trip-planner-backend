package com.tripPlanner.project.component;

import com.tripPlanner.project.dto.notification.NotificationDTO;
import com.tripPlanner.project.entity.notification.NotificationEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationStreamer {


    // 유저별 다중 탭 지원을 위해 List 보관
    private final ConcurrentMap<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /** 유저가 구독할 때마다 Emitter 발급 */
    public SseEmitter subscribe(Long userId) {
        // 무한 타임아웃 (전역 설정도 함께 권장: spring.mvc.async.request-timeout=0)
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        // 연결 확인용 이벤트 한 번 보내기
        try {
            emitter.send(SseEmitter.event().name("hello").data("ok"));
        } catch (IOException ignored) {}

        return emitter;
    }

    /** 저장된 알림을 실시간으로 해당 유저에게 브로드캐스트 */
    public void push(NotificationEntity n) {
        var list = emitters.getOrDefault(n.getRecipientId(), new CopyOnWriteArrayList<>());
        for (SseEmitter e : list) {
            try {
                e.send(SseEmitter.event()
                        .name("notification")
                        .id(String.valueOf(n.getId()))
                        .data(NotificationDTO.from(n)));
            } catch (IOException ex) {
                remove(n.getRecipientId(), e); // 끊긴 연결 정리
            }
        }
    }

    private void remove(Long userId, SseEmitter e) {
        var list = emitters.get(userId);
        if (list != null) list.remove(e);
    }

}
