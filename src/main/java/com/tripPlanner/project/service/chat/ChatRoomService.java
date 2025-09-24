package com.tripPlanner.project.service.chat;

import com.tripPlanner.project.dto.chat.ChatRoomMemberDto;
import com.tripPlanner.project.dto.chat.ResponseChatRoomDto;
import com.tripPlanner.project.dto.chat.ResponseChatRoomDto2;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import com.tripPlanner.project.entity.chat.ChatRoomMemberEntity;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.repository.chat.ChatMessageRepository;
import com.tripPlanner.project.repository.chat.ChatRoomMemberRepository;
import com.tripPlanner.project.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    /*
        1. myId, targetId 두 유저가 함께 속한 방이 있으면 그 roomId 반환
        2. 없으면 방 생성 + 멤버 2명 저장 후 roomId 반환
        3. 첫 메시지 저장(MongoDB)
        4. roomId 반환
     */
    @Transactional
    public Long createChatRoom(Long myId, Long targetId, String firstMessage) {

        if (firstMessage == null || firstMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("firstMessage is required");
        }

        // 1. 기존 방 탐색
        List<Long> existing = chatRoomMemberRepository.findDmRoomIdsBetween(myId, targetId);
        Long roomId;
        if(!existing.isEmpty()){
            return existing.get(0); // 첫 번째 방 사용
        } else {

            // 2. 새 방 생성
            ChatRoomEntity room = new ChatRoomEntity("대화방", new Date());
            chatRoomRepository.save(room);

            // 3. 유저 조회
            UserEntity me = userRepository.findById(myId).orElseThrow();
            UserEntity target = userRepository.findById(targetId).orElseThrow();

            // 4. 멤버 2명 저장
            ChatRoomMemberEntity member1 = new ChatRoomMemberEntity(room, me, me.getNickname());
            ChatRoomMemberEntity member2 = new ChatRoomMemberEntity(room, target, target.getNickname());
            chatRoomMemberRepository.save(member1);
            chatRoomMemberRepository.save(member2);

            roomId = room.getId();
        }

        // 5. 첫 메시지 저장 (MongoDB)  ← 트랜잭션 분리(기본)
        // JPA 커밋 이후에만 MongoDB 저장(채팅방 저장 후 메시지 저장을 순차적으로 하기위해)

        registerFirstMessageAfterCommit(roomId, myId, firstMessage.trim());

        return roomId;
    }


    @Transactional
    public List<ResponseChatRoomDto> findChatRoomList() {
        List<ChatRoomEntity> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream().map(ResponseChatRoomDto::of).collect(Collectors.toList());
    }


    // 채팅방 가져오기 테스트
    @Transactional
    public List<ResponseChatRoomDto2> findChatRoomList2() {
        List<ChatRoomEntity> chatRooms = chatRoomRepository.findAllWithMembers();
        return chatRooms.stream()
                .map(r -> new ResponseChatRoomDto2(
                        r.getId(),
                        r.getTitle(),
                        r.getNewDate(),
                        r.getMembers().stream()
                                .map(m -> new ChatRoomMemberDto(m.getUserEntity().getId(), m.getNickname(), m.getUserEntity().getAvatarUrl()))
                                .toList(),
                        r.getLastMessage(),
                        r.getLastMessageAt()
                ))
                .toList();
    }


    private void registerFirstMessageAfterCommit(Long roomId, Long senderId, String message) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                chatMessageRepository
                        .save(new ChatMessageEntity(null, roomId, message, senderId, new Date()))
                        .subscribe(
                                saved -> log.info("첫 메시지 저장 성공: {}", saved.getId()),
                                error -> log.error("첫 메시지 저장 실패", error)
                        );
            }
        });
    }



}
