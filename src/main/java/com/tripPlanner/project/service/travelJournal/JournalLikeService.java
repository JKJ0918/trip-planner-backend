package com.tripPlanner.project.service.travelJournal;

import com.tripPlanner.project.entity.travelJournal.JournalLikeEntity;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.repository.travelJournal.JournalLikeRepository;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JournalLikeService {

    private final JournalLikeRepository journalLikeRepository;
    private final TravelJournalRepository travelJournalRepository;
    private final UserRepository userRepository;

    // private final NotificationPublisher notificationPublisher; // 알림 발송용(인터페이스, 아래 예시)

    @Transactional
    public void like(Long travelJournalId, Long userId){
        if(journalLikeRepository.existsByTravelJournalLikeEntity_IdAndUserId(travelJournalId, userId)){
            return; // 멱등 : 이미 눌렸으면 종료
        }

        TravelJournalEntity travelJournalEntity = travelJournalRepository.getReferenceById(travelJournalId);

        JournalLikeEntity journalLikeEntity = JournalLikeEntity.builder()
                .travelJournalLikeEntity(travelJournalEntity)
                .userId(userId)
                .build();
        journalLikeRepository.save(journalLikeEntity);

        // 닉네임 조회 후 알람 전손
        String nickname = userRepository.findNicknameById(userId).orElse("알 수 없음");
    }

    // 좋아요 취소
    @Transactional
    public void unlike(Long travelJournalId, Long userId){
        journalLikeRepository.deleteByTravelJournalLikeEntity_IdAndUserId(travelJournalId, userId);
    }

    //좋아요 수 카운트
    @Transactional(readOnly = true)
    private long count(Long travelJournalId){
        return journalLikeRepository.countByTravelJournalLikeEntity_Id(travelJournalId);
    }

    //좋아요 여부 확인
    @Transactional(readOnly = true)
    private boolean likedByMe(Long travelJournalId, Long userId){
        return journalLikeRepository.existsByTravelJournalLikeEntity_IdAndUserId(travelJournalId, userId);
    }

}
