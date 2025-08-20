package com.tripPlanner.project.repository.travelJournal;

import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TravelJournalRepository extends JpaRepository<TravelJournalEntity, Long>, JpaSpecificationExecutor<TravelJournalEntity> {
    // List<TravelJournalEntity> findByUserId(String userId); // 토큰의 이름과 소셜 타입으로 찾을 예정임
    List<TravelJournalEntity> findByIsPublicTrueOrderByCreatedAtDesc();

    Page<TravelJournalEntity> findByIsPublicTrue(Pageable pageable);

    @Query("SELECT j FROM TravelJournalEntity j WHERE j.isPublic = true AND " +
            "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.locationSummary) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TravelJournalEntity> searchPublicByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 마이페이지 내가쓴 게시글 불러오기
    Page<TravelJournalEntity> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);


}
