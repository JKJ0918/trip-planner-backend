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
public class ResponseChatRoomDto {

    private Long id;
    private String title;
    private Date createDate;

    public static ResponseChatRoomDto of(ChatRoomEntity chatRoom) {
        return new ResponseChatRoomDto(chatRoom.getId(), chatRoom.getTitle(),
                chatRoom.getNewDate());
    }
}
