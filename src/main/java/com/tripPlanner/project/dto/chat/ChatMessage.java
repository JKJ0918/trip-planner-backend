package com.tripPlanner.project.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessage {

    private Long id;
    private Long userId; // 유저 아이디
    private String nickname; // 유저 닉네임
    private String message; // 메시지 내용

    public ChatMessage(Long id, Long userId, String nickname, String message){
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.message = message;

    }
}
