package com.tripPlanner.project.repository.TravelJournal;

import com.tripPlanner.project.entity.TravelJournal.TravelJournalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelJournalRepository extends JpaRepository<TravelJournalEntity, Long> {
    // List<TravelJournalEntity> findByUserId(String userId); // 토큰의 이름과 소셜 타입으로 찾을 예정임
}
