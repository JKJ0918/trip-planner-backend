package com.tripPlanner.project.controller.chat;

import com.tripPlanner.project.dto.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations template;

    // 채팅 리스트 반환
    @GetMapping("/chat/{id}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @PathVariable("id") Long id
    ){
        // 임시로 리스트 형식 구현, 실제로는 db 연동 예정
        ChatMessage test = new ChatMessage(1L, 2L, "testnick", "기존에 있던 메시지 입니다.");
        return ResponseEntity.ok().body(List.of(test));
    }

    // 메시지 송신 및 수신, / pub가 생략된 모습, 클라이언트 단에선 /pub/message로 요청
    @MessageMapping("/message")
    public ResponseEntity<Void> receiveMessage(@RequestBody ChatMessage chat) {
        // 메시지를 해당 채팅방 구독자들에게 전송
        template.convertAndSend("/sub/chatroom/1", chat);
        return ResponseEntity.ok().build();
        // 추후 roomId에 따라 데이터베이스(MongoDB)에서 데이터 리스트를 가져오는 로직이 필요
    }

}
