package com.tripPlanner.project.repository.notification;

import com.tripPlanner.project.entity.notification.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.management.Notification;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    long countByRecipientIdAndIsReadFalse(Long recipientId);

    @Modifying
    @Transactional
    @Query("update NotificationEntity n set n.isRead = true where n.id = :id and n.recipientId = :userId")
    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying @Transactional
    @Query("update NotificationEntity n set n.isRead = true where n.recipientId = :userId and n.isRead = false")
    int markAllRead(@Param("userId") Long userId);

}
