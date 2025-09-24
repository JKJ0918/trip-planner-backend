package com.tripPlanner.project.controller.chat;

import com.tripPlanner.project.dto.CustomUserDetails;
import com.tripPlanner.project.dto.chat.*;
import com.tripPlanner.project.dto.ws.WsUserPrincipal;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.chat.ChatRoomService;
import com.tripPlanner.project.service.chat.ChatService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // 채팅방 생성
    @PostMapping("/create")
    public ResponseEntity<ResponseChatRoomDto> createChatRoom(
            HttpServletRequest request,
            @RequestBody RequestChatRoomDto requestChatRoomDto

    ) {
        Long myId = extractUserIdFromRequest(request);
        Long targetId = requestChatRoomDto.getTargetUserId();
        String firstMessage = requestChatRoomDto.getFirstMessage();

        Long roomId = chatRoomService.createChatRoom(myId, targetId, firstMessage);

        return ResponseEntity.ok(new ResponseChatRoomDto(roomId));
    }

    // 채팅방 목록 가져오기 (기존 삭제 예정)
    @GetMapping("/chatList")
    public ResponseEntity<List<ResponseChatRoomDto>> getChatRoomList() {
        List<ResponseChatRoomDto> responses = chatRoomService.findChatRoomList();
        return ResponseEntity.ok().body(responses);
    }

    // 채팅방 목록 가져오기2
    @GetMapping("/chatList2")
    public ResponseEntity<List<ResponseChatRoomDto2>> getChatRoomList2() {

        List<ResponseChatRoomDto2> responses = chatRoomService.findChatRoomList2();
        return ResponseEntity.ok().body(responses);
    }

    // 이전 채팅 내용 조회
    @GetMapping("/find/chat/list/{id}")
    public Mono<ResponseEntity<List<ResponseMessageDto>>> find(@PathVariable("id") Long id){
        Flux<ResponseMessageDto> response = chatService.findChatMessages(id);
        return response.collectList().map(ResponseEntity::ok);
    }

    // 메시지 송신 및 수신, / pub가 생략된 모습, 클라이언트 단에선 /pub/message로 요청
    @MessageMapping("/message")
    public Mono<ResponseEntity<Void>> receiveMessage( @RequestBody RequestMessageDto chat,
                                                      SimpMessageHeaderAccessor accessor) {

        Map<String, Object> attrs = accessor.getSessionAttributes();
        Long writerId = (attrs != null) ? (Long) attrs.get("userId") : null;


        return chatService.saveChatMessage(chat, writerId).flatMap(message -> {
            // 메시지를 해당 채팅방 구독자들에게 전송
            template.convertAndSend("/sub/chatroom/" + chat.getRoomId(),
                    ResponseMessageDto.of(message));

            return Mono.just(ResponseEntity.ok().build()); // @MessageMapping 메서드 자체의 리턴값
        });

        // 추후 roomId에 따라 데이터베이스(MongoDB)에서 데이터 리스트를 가져오는 로직이 필요
    }

    // 토큰 추출
    private String extractAccessToken(HttpServletRequest request){
        // 1. OAuth2 방식: 쿠키에서 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. JWT 방식: 헤더에서 찾기
        String header = request.getHeader("access");

        return null;
    }

    // 토큰에서 Long userId 추출
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String token = extractAccessToken(request);

        // 토큰 없을 시 null 반환
        if (!org.springframework.util.StringUtils.hasText(token)) {
            return null;
        }

        String name = jwtUtil.getUsername(token);
        String socialType = jwtUtil.getSocialType(token);

        UserEntity user = new UserEntity();
        if(socialType.equals("localUser")){
            user = userRepository.findByUsernameAndSocialType(name, "localUser");
        }else {
            user = userRepository.findByNameAndSocialType(name, socialType);
        }

        return user.getId();
    }



}
