package com.tripPlanner.project.dto.chat;

import com.tripPlanner.project.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomMemberDto {
    private String nickname;
    private Long chatroomId;
    private Long userId;
    private String avatarUrl;

    public ChatRoomMemberDto(Long userId, String nickname, String avatarUrl) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
    }
}
