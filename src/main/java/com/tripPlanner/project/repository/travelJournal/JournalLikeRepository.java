package com.tripPlanner.project.repository.travelJournal;

import com.tripPlanner.project.entity.travelJournal.JournalLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JournalLikeRepository extends JpaRepository<JournalLikeEntity, Long> {

    // 파생 쿼리 메서드는 “컬럼명”이 아니라 “엔티티 필드명” 기준으로 만듬

    // 내가 좋아요 눌렀는지
    boolean existsByTravelJournalLikeEntity_IdAndUserId(Long travelJournalId, Long userId);

    // 좋아요 갯수
    long countByTravelJournalLikeEntity_Id(Long travelJournalId);

    // 좋아요 취소
    int deleteByTravelJournalLikeEntity_IdAndUserId(Long travelJournalId, Long userId);

    @Query("SELECT jl.travelJournalLikeEntity.id AS journalId, COUNT(jl) AS cnt " +
            "FROM JournalLikeEntity jl " +
            "WHERE jl.travelJournalLikeEntity.id IN :journalIds " +
            "GROUP BY jl.travelJournalLikeEntity.id")

    List<JournalLikeCount> countByJournalIds(@Param("journalIds") List<Long> journalIds);


}
