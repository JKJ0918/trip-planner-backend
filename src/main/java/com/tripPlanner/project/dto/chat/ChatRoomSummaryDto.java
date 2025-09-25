package com.tripPlanner.project.dto.chat;

import com.tripPlanner.project.entity.chat.ChatRoomEntity;
import lombok.*;

import java.util.Date;

/**
 * 사이드바 카드 갱신용 DTO (개인 큐로 전송)
 * /user/queue/chatrooms/summary
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomSummaryDto {

    // 채팅방인원에게 전달할 요약 정보
    private Long roomId;
    private String lastMessage;     // 마지막 메시지(미리보기)
    private long lastMessageAt;     // epoch millis
    private Long lastWriterId;
    private int unreadCount;        // "해당 사용자" 기준 미확인 개수


}
