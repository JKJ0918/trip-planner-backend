package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessageEntity, String> {

    // 채팅방 찾기
    Flux<ChatMessageEntity> findAllByRoomId(Long roomId);
}
