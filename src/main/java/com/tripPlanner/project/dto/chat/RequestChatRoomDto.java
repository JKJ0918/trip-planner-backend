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

    // 프론트 엔드에서 요청하는 body의 이름과 맞추시오.
    private Long targetUserId;
    private String firstMessage;

}
