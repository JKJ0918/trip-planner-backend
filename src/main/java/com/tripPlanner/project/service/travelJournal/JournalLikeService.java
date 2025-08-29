package com.tripPlanner.project.service.travelJournal;

import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.entity.notification.NotificationEntity;
import com.tripPlanner.project.entity.travelJournal.JournalLikeEntity;
import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.repository.travelJournal.JournalLikeRepository;
import com.tripPlanner.project.repository.travelJournal.TravelJournalRepository;
import com.tripPlanner.project.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JournalLikeService {

    private final JournalLikeRepository journalLikeRepository;
    private final TravelJournalRepository travelJournalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void like(Long travelJournalId, Long userId){
        if(journalLikeRepository.existsByTravelJournalLikeEntity_IdAndUserId(travelJournalId, userId)){
            return; // 멱등 : 이미 눌렸으면 종료
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자 입니다."));

        TravelJournalEntity travelJournalEntity = travelJournalRepository.getReferenceById(travelJournalId);

        JournalLikeEntity journalLikeEntity = JournalLikeEntity.builder()
                .travelJournalLikeEntity(travelJournalEntity)
                .userId(userId)
                .build();
        journalLikeRepository.save(journalLikeEntity);

        // 게시글 좋아요 알람
        // 1) 저장된 좋아요 엔티티 : saved
        JournalLikeEntity saved = journalLikeRepository.save(journalLikeEntity);

        // 2) 알람 받을 사람(게시글 작성자) 찾기 (recipientId)
        Long recipientId = journalLikeEntity.getTravelJournalLikeEntity().getUser().getId();

        // 3) 좋아요 누른 사용자의 닉네임
        String actorNickname = user.getNickname();

        // 4) 자기 자신에게는 알림 보내지 않기
        if (!recipientId.equals(userId)) {
            notificationService.createAndSend(
                    recipientId,                     // 받는 사람
                    userId,                          // 행동 주체(댓글 단 사람)
                    NotificationEntity.Type.LIKE, // 알림 타입
                    travelJournalEntity.getId(),   // postId에 해당(여행일지 id)
                    saved.getId(),                   // commentId
                    actorNickname + "님이" + travelJournalEntity.getTitle() + "게시물을 좋아합니다.",
                    // 프론트 라우팅에 맞춰 링크 수정하세요.
                    // (당신 프로젝트가 /posts/[id]면 "/posts/" + journalId)
                    // (만약 /journals/[id] 라우팅이면 "/journals/" + journalId)
                    "/posts/" + travelJournalEntity.getId() + "#comment-" + saved.getId()
            );
        }


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
