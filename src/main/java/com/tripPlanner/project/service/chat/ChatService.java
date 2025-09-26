package com.tripPlanner.project.service.chat;

import com.tripPlanner.project.dto.chat.ChatRoomSummaryDto;
import com.tripPlanner.project.dto.chat.RequestMessageDto;
import com.tripPlanner.project.dto.chat.ResponseMessageDto;
import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import com.tripPlanner.project.entity.chat.ChatRoomMemberEntity;
import com.tripPlanner.project.entity.comments.CommentEntity;
import com.tripPlanner.project.repository.chat.ChatMessageRepository;
import com.tripPlanner.project.repository.chat.ChatRoomMemberRepository;
import com.tripPlanner.project.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository; // JPA (blocking)
    private final SimpMessagingTemplate template;

    @Transactional
    public Flux<ResponseMessageDto> findChatMessages(Long id) {
        Flux<ChatMessageEntity> chatMessages = chatMessageRepository.findAllByRoomId(id);
        return chatMessages.map(ResponseMessageDto::of);
    }

    @Transactional
    public Mono<ChatMessageEntity> saveChatMessage(RequestMessageDto chat, Long writerId) {

        ChatMessageEntity entity = new ChatMessageEntity(
                chat.getRoomId(),
                chat.getContent(),
                writerId,
                new Date()
        );

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chat.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 요청"));

        chatRoomEntity.setLastMessage(chat.getContent());
        chatRoomEntity.setLastMessageAt(chat.getCreatedDate());


        return chatMessageRepository.save(entity).flatMap(saved -> {
            // 요약 브로드캐스트(사이드바 갱신)
            return broadcastRoomSummaryForAll(chat.getRoomId(), saved, writerId)
                    .thenReturn(saved);
        });
    }

    // 요약 메시지 브로드 캐스트
    // 자바에서 브로드캐스트(Broadcast)는 특정 이벤트가 발생했을 때 해당 이벤트를
    // 모든 관련 시스템(또는 앱의 구성 요소)에게 동시에 알리고 전달하는 방식
    // 마지막 메시지를 기준으로 방 참여자 전원에게 개인 큐 요약 이벤트 전송
    // writerId: 마지막 메시지 작성자
    public Mono<Void> broadcastRoomSummaryForAll(Long roomId, ChatMessageEntity lastMessage, Long writerId) {

        // 1) 방 참여자 userId 리스트 조회 (blocking JPA) → 바로 사용
        List<Long> memberIds = chatRoomMemberRepository.findUserIdsByRoomId(roomId);
        if(memberIds.isEmpty()) {
            return Mono.empty();
        }

        // 2) 각 사용자별 unread 계산 후 convertAndSendToUser
        //    lastReadAt을 모르면 "null → epoch 0"으로 간주하거나 전용 조회 추가
        //    여기서는 간단히 멤버 엔티티를 다시 불러 unread 계산 예시를 보여줌
        List<ChatRoomMemberEntity> members = chatRoomMemberRepository.findMembersByRoomId(roomId);

        for (ChatRoomMemberEntity member : members) {
            Long userId = member.getUserEntity().getId();
            Date lastReadAt = member.getLastReadAt(); // null 가능

            Mono<Long> unreadMono;
            if (lastReadAt == null) {
                unreadMono = chatMessageRepository.countByRoomIdAndCreatedDateAfter(roomId, new Date(0));
            } else {
                unreadMono = chatMessageRepository.countByRoomIdAndCreatedDateAfter(roomId, lastReadAt);
            }

            unreadMono.defaultIfEmpty(0L).subscribe(unreadCount -> {
                ChatRoomSummaryDto dto = ChatRoomSummaryDto.builder()
                        .roomId(roomId)
                        .lastMessage(lastMessage.getContent())
                        .lastMessageAt(lastMessage.getCreatedDate().getTime())
                        .lastWriterId(writerId)
                        .unreadCount(unreadCount.intValue())
                        .build();
                // 개인 큐 전송: /user/{uid}/queue/chatrooms/summary
                template.convertAndSendToUser(
                        String.valueOf(userId), "/queue/chatrooms/summary", dto
                );
            });

        } // end for
        return Mono.empty();
    }// end broadcastRoomSummaryForAll


}
