package com.tripPlanner.project.entity.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(columnList = "recipient_id, is_read, created_at")
})
@Getter
@Setter
public class NotificationEntity {
    public enum Type { COMMENT, LIKE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient_id", nullable = false) // 알람을 받는 자
    private Long recipientId;

    @Column(name = "actor_id", nullable = false) // 좋아요 및 댓글 작성자
    private Long actorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Type type;

    @Column(name = "post_id", nullable = false) // 게시물 Id
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId; // 댓글일 때만

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String link;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
