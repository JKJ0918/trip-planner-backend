package com.tripPlanner.project.dto.notification;

import com.tripPlanner.project.entity.notification.NotificationEntity;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long id,
        String type,     // "COMMENT" | "LIKE"
        String message,
        String link,
        boolean isRead,
        Long postId,
        Long commentId,
        Long actorId,
        LocalDateTime createdAt
) {
    public static NotificationDTO from(NotificationEntity n) {
        return new NotificationDTO(
                n.getId(),
                n.getType().name(),
                n.getMessage(),
                n.getLink(),
                n.isRead(),
                n.getPostId(),
                n.getCommentId(),
                n.getActorId(),
                n.getCreatedAt()
        );
    }
}