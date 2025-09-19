package com.tripPlanner.project.dto.chat;

import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestChatRoomDto {

    private Long targetUserId;
    private String firstMessage;

}
