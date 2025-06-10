package com.tripPlanner.project.repository.comments;

import com.tripPlanner.project.entity.comments.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByTravelJournal_IdOrderByCreatedAtAsc(Long journalId);  // 수정
}
