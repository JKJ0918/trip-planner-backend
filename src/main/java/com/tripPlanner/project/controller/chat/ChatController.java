package com.tripPlanner.project.controller.chat;

import com.tripPlanner.project.dto.chat.RequestMessageDto;
import com.tripPlanner.project.dto.chat.ResponseMessageDto;
import com.tripPlanner.project.dto.chat.RequestChatRoomDto;
import com.tripPlanner.project.dto.chat.ResponseChatRoomDto;
import com.tripPlanner.project.service.chat.ChatRoomService;
import com.tripPlanner.project.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    /*
    // 채팅 리스트 반환
    @GetMapping("/chat/{id}")
    public ResponseEntity<List<ResponseMessageDto>> getChatMessages(
            @PathVariable("id") Long id
    ){
        // 임시로 리스트 형식 구현, 실제로는 db 연동 예정
        ResponseMessageDto test = new ResponseMessageDto(1L, 2L, "testnick", "기존에 있던 메시지 입니다.");
        return ResponseEntity.ok().body(List.of(test));
    }
    */

    // 메시지 송신 및 수신, / pub가 생략된 모습, 클라이언트 단에선 /pub/message로 요청
    @MessageMapping("/message")
    public Mono<ResponseEntity<Void>> receiveMessage(@RequestBody RequestMessageDto chat) {

        return chatService.saveChatMessage(chat).flatMap(message -> {
            // 메시지를 해당 채팅방 구독자들에게 전송
            template.convertAndSend("/sub/chatroom/" + chat.getRoomId(),
                    ResponseMessageDto.of(message));
            return Mono.just(ResponseEntity.ok().build());
        });

        // 추후 roomId에 따라 데이터베이스(MongoDB)에서 데이터 리스트를 가져오는 로직이 필요
    }

    // 채팅방 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseChatRoomDto> createChatRoom(
            @RequestBody RequestChatRoomDto requestChatRoomDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatRoomService.createChatRoom(requestChatRoomDto));
    }

    // 채팅방 목록 가져오기
    @GetMapping("/chatList")
    public ResponseEntity<List<ResponseChatRoomDto>> getChatRoomList() {
        List<ResponseChatRoomDto> responses = chatRoomService.findChatRoomList();
        return ResponseEntity.ok().body(responses);
    }
    
    // 이전 채팅 내용 조회
    @GetMapping("/find/chat/list/{id}")
    public Mono<ResponseEntity<List<ResponseMessageDto>>> find(@PathVariable("id") Long id){
        Flux<ResponseMessageDto> response = chatService.findChatMessages(id);
        return response.collectList().map(ResponseEntity::ok);
    }


}
