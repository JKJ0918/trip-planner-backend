package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import reactor.core.publisher.Flux;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

}
