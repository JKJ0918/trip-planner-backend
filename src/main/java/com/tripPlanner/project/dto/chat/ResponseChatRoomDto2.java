package com.tripPlanner.project.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseChatRoomDto2 {

    private Long roomId;
    private String title;
    private Date createDate;
    List<ChatRoomMemberDto> members;
    private String lastMessage; // 마지막 메시지
    private LocalDate lastMessageAt; // 마지막 메시지 날짜
    
}
