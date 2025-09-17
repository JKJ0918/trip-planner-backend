package com.tripPlanner.project.dto.chat;

import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class RequestChatRoomDto {

    private Long id;
    private String title;
    private Date createDate;

    public static RequestChatRoomDto of(ChatRoomEntity chatRoom) {
        return new RequestChatRoomDto(chatRoom.getId(), chatRoom.getTitle(),
                chatRoom.getNewDate());
    }

}
