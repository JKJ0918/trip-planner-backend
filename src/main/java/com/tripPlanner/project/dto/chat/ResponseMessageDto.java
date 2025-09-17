package com.tripPlanner.project.dto.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ResponseMessageDto {

    private String id;        // ObjectId.toHexString() 으로 변환
    private Long roomId;
    private String content;
    private Long writerId;
    private Date createdDate;

    public static ResponseMessageDto of(ChatMessageEntity entity) {
        return new ResponseMessageDto(
                entity.getId() != null ? entity.getId().toHexString() : null, // ObjectId → String
                entity.getRoomId(),
                entity.getContent(),
                entity.getWriterId(),
                entity.getCreatedDate()
        );
    }
}
