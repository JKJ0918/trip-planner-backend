package com.tripPlanner.project.repository.comments;

import com.tripPlanner.project.entity.comments.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByTravelJournal_IdOrderByCreatedAtAsc(Long journalId);  // 수정

    Page<CommentEntity> findByTravelJournalIdAndParentIsNull(Long journalId, Pageable pageable);

    Page<CommentEntity> findByParentId(Long parentId, Pageable pageable); // 직속(부모) 대댓글(자식) 반환


    // 기본 정렬용
    Page<CommentEntity> findByTravelJournalIdAndParentIsNullOrderByCreatedAtDesc(Long journalId, Pageable pageable);

    // 좋아요 정렬용 - DB에서 전체 가져온 뒤 Java에서 정렬
    List<CommentEntity> findByTravelJournalIdAndParentIsNull(Long journalId);

    // 최신순
    @EntityGraph(attributePaths = {"user"})  // ← 작성자 함께 로딩
    @Query("""
              SELECT c
              FROM CommentEntity c
              WHERE c.travelJournal.id = :journalId
                AND c.parent IS NULL
              ORDER BY c.createdAt DESC
            """)
    Page<CommentEntity> findTopLevelCommentsByJournalIdOrderByCreatedAtDesc(
            @Param("journalId") Long journalId,
            Pageable pageable
    );

    // 인기순
    @EntityGraph(attributePaths = {"user"})  // ← 작성자 함께 로딩
    @Query("""
              SELECT c
              FROM CommentEntity c
              WHERE c.travelJournal.id = :journalId
                AND c.parent IS NULL
              ORDER BY SIZE(c.likes) DESC
            """)
    Page<CommentEntity> findTopLevelCommentsByJournalIdOrderByLikesDesc(
            @Param("journalId") Long journalId,
            Pageable pageable
    );




}
