package com.tripPlanner.project.service.chat;

import com.tripPlanner.project.dto.chat.RequestMessageDto;
import com.tripPlanner.project.dto.chat.ResponseMessageDto;
import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import com.tripPlanner.project.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public Flux<ResponseMessageDto> findChatMessages(Long id) {
        Flux<ChatMessageEntity> chatMessages = chatMessageRepository.findAllByRoomId(id);
        return chatMessages.map(ResponseMessageDto::of);
    }

    @Transactional
    public Mono<ChatMessageEntity> saveChatMessage(RequestMessageDto chat, Long writerId) {
        return chatMessageRepository.save(
                new ChatMessageEntity(chat.getRoomId(), chat.getContent(), writerId,
                        new Date()));
    }

}
