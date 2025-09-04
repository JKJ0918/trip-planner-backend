package com.tripPlanner.project.repository.travelJournal;

import com.tripPlanner.project.dto.myPage.MeLikePostDTO;
import com.tripPlanner.project.entity.travelJournal.JournalLikeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 좋아요 수 계산
    @Query("SELECT jl.travelJournalLikeEntity.id AS journalId, COUNT(jl) AS cnt " +
            "FROM JournalLikeEntity jl " +
            "WHERE jl.travelJournalLikeEntity.id IN :journalIds " +
            "GROUP BY jl.travelJournalLikeEntity.id")

    List<JournalLikeCount> countByJournalIds(@Param("journalIds") List<Long> journalIds);

    // 마이페이지 - 좋아요 한게시물
    @Query(
            value = """
    select new com.tripPlanner.project.dto.myPage.MeLikePostDTO(
      j.id,
      j.title,
      u.nickname,
      j.createdAt
    )
    from JournalLikeEntity l
    join l.travelJournalLikeEntity j
    join j.user u
    where l.userId = :userId
      and ( :afterTravel is null or coalesce(j.isAfterTravel, false) = :afterTravel )
    """,
            countQuery = """
    select count(l)
    from JournalLikeEntity l
    join l.travelJournalLikeEntity j
    where l.userId = :userId
      and ( :afterTravel is null or coalesce(j.isAfterTravel, false) = :afterTravel )
    """
    )
    Page<MeLikePostDTO> findMyLikedPosts(@Param("userId") Long userId,
                                         @Param("afterTravel") Boolean afterTravel,
                                         Pageable pageable);



}
