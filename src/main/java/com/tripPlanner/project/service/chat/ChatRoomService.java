package com.tripPlanner.project.service.chat;

import com.tripPlanner.project.dto.chat.RequestChatRoomDto;
import com.tripPlanner.project.dto.chat.ResponseChatRoomDto;
import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import com.tripPlanner.project.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ResponseChatRoomDto createChatRoom(RequestChatRoomDto requestChatRoomDto) {
        ChatRoomEntity chatRoom = new ChatRoomEntity(requestChatRoomDto.getTitle(), new Date());
        return ResponseChatRoomDto.of(chatRoomRepository.save(chatRoom));
    }

    @Transactional
    public List<ResponseChatRoomDto> findChatRoomList() {
        List<ChatRoomEntity> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream().map(ResponseChatRoomDto::of).collect(Collectors.toList());
    }

}
