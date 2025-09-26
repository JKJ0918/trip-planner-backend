package com.tripPlanner.project.repository.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("select distinct r from ChatRoomEntity r left join fetch r.members")
    List<ChatRoomEntity> findAllWithMembers(); // members는 ChatRoomEntity 연관 매핑한 이름
    
}
