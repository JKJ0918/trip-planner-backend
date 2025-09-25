package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessageEntity, String> {

    // 채팅방 찾기 (이건 첫 메시지 이후 계속 저장 용도)
    Flux<ChatMessageEntity> findAllByRoomId(Long roomId);

    // 특정 시각 이후의 미확인 메시지 개수
    Mono<Long> countByRoomIdAndCreatedDateAfter(Long roomId, Date createdDate);
    
    // 마지막 메시지 1개(미리보기용) — 이미 저장된 message가 있으므로 선택사항 * 사용하지 않을 예정
    Mono<ChatMessageEntity> findFirstByRoomIdOrderByCreatedDateDesc(Long roomId);

}
