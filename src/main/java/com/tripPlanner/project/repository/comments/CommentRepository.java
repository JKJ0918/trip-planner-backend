package com.tripPlanner.project.repository.comments;

import com.tripPlanner.project.entity.comments.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByTravelJournal_IdOrderByCreatedAtAsc(Long journalId);  // 수정

    Page<CommentEntity> findByTravelJournalIdAndParentIsNullOrderByCreatedAtAsc(Long journalId, Pageable pageable); // 댓글 페이징 처리

    List<CommentEntity> findByParentIdOrderByCreatedAtAsc(Long parentId); // 직속(부모) 대댓글(자식) 반환
}
