package com.tripPlanner.project.service.chat;

import com.tripPlanner.project.dto.chat.RequestMessageDto;
import com.tripPlanner.project.dto.chat.ResponseMessageDto;
import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import com.tripPlanner.project.repository.chat.ChatMessageRepository;
import com.tripPlanner.project.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public Flux<ResponseMessageDto> findChatMessages(Long id) {
        Flux<ChatMessageEntity> chatMessages = chatMessageRepository.findAllByRoomId(id);
        return chatMessages.map(ResponseMessageDto::of);
    }

    @Transactional
    public Mono<ChatMessageEntity> saveChatMessage(RequestMessageDto chat, Long writerId) {

        // 메시지 최신화, 트랜젝션 후 실행되도록 수정 필요.
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chat.getRoomId())
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        chatRoom.setLastMessage(chat.getContent());
        chatRoom.setLastMessageAt(LocalDate.now());




        return chatMessageRepository.save(
                new ChatMessageEntity(chat.getRoomId(), chat.getContent(), writerId,
                        new Date()));
    }

}
