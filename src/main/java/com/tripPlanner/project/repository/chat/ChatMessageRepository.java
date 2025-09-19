package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessageEntity, String> {

    // 채팅방 찾기 (이건 첫 메시지 이후 계속 저장 용도)
    Flux<ChatMessageEntity> findAllByRoomId(Long roomId);
    
    // 채팅방찾기
    List<ChatMessageEntity> findByRoomIdOrderByCreatedDateAsc(Long roomId);
}
