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

    // 사용자의 알림을 최신순(CreatedAtDesc 내림차순) 으로 페이징 조회
    Page<NotificationEntity> findByRecipientIdOrderByCreatedAtDesc(Long recipientId, Pageable pageable);

    // 해당 사용자의 안 읽은(isRead = false) 알림 개수를 반환
    long countByRecipientIdAndIsReadFalse(Long recipientId);

    @Modifying
    @Transactional
    @Query("update NotificationEntity n set n.isRead = true where n.id = :id and n.recipientId = :userId")
    int markRead(@Param("id") Long id, @Param("userId") Long userId);

    @Modifying @Transactional
    @Query("update NotificationEntity n set n.isRead = true where n.recipientId = :userId and n.isRead = false")
    int markAllRead(@Param("userId") Long userId);

}
