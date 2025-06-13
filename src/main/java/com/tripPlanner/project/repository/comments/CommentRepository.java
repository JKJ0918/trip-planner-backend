package com.tripPlanner.project.repository.comments;

import com.tripPlanner.project.entity.comments.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByTravelJournal_IdOrderByCreatedAtAsc(Long journalId);  // 수정

    Page<CommentEntity> findByTravelJournalIdAndParentIsNull(Long journalId, Pageable pageable);

    List<CommentEntity> findByParentIdOrderByCreatedAtAsc(Long parentId); // 직속(부모) 대댓글(자식) 반환

    // 기본 정렬용
    Page<CommentEntity> findByTravelJournalIdAndParentIsNullOrderByCreatedAtDesc(Long journalId, Pageable pageable);

    // 좋아요 정렬용 - DB에서 전체 가져온 뒤 Java에서 정렬
    List<CommentEntity> findByTravelJournalIdAndParentIsNull(Long journalId);

    // 최신순 정렬
    @Query("SELECT c FROM CommentEntity c WHERE c.travelJournal.id = :journalId AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<CommentEntity> findTopLevelCommentsByJournalIdOrderByCreatedAtDesc(
            @Param("journalId") Long journalId,
            Pageable pageable
    );

    // 좋아요 많은 순 정렬 (JPQL의 SIZE 함수 사용)
    @Query("SELECT c FROM CommentEntity c WHERE c.travelJournal.id = :journalId AND c.parent IS NULL ORDER BY SIZE(c.likes) DESC")
    Page<CommentEntity> findTopLevelCommentsByJournalIdOrderByLikesDesc(
            @Param("journalId") Long journalId,
            Pageable pageable
    );
}
