package com.tripPlanner.project.service.notification;

import com.tripPlanner.project.component.NotificationStreamer;
import com.tripPlanner.project.entity.notification.NotificationEntity;
import com.tripPlanner.project.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class NotificationService {
    private final NotificationRepository repo;
    private final NotificationStreamer streamer; // 아래 4)에서 만듭니다.

    public NotificationService(NotificationRepository repo, NotificationStreamer streamer) {
        this.repo = repo;
        this.streamer = streamer;
    }

    @Transactional
    public NotificationEntity createAndSend(
            Long recipientId, Long actorId,
            NotificationEntity.Type type,
            Long postId, Long commentId,
            String message, String link) {

        if (Objects.equals(recipientId, actorId)) return null; // 자기 자신이면 알림 안 보냄(선택)

        NotificationEntity n = new NotificationEntity();
        n.setRecipientId(recipientId);
        n.setActorId(actorId);
        n.setType(type);
        n.setPostId(postId);
        n.setCommentId(commentId);
        n.setMessage(message);
        n.setLink(link);

        NotificationEntity saved = repo.save(n);
        streamer.push(saved); // 실시간으로 쏘기 (실패해도 DB에는 저장됨)
        return saved;
    }
}
