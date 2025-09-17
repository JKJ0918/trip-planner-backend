package com.tripPlanner.project.dto.chat;

import com.tripPlanner.project.entity.chat.ChatMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestMessageDto {

    private String id;
    private Long roomId;
    private String content;
    private Long writerId;
    private Date createdDate;

    public static RequestMessageDto of(ChatMessageEntity entity) {
        return new RequestMessageDto(
                entity.getId() != null ? entity.getId().toHexString() : null, // ObjectId → String
                entity.getRoomId(),
                entity.getContent(),
                entity.getWriterId(),
                entity.getCreatedDate()
        );
    }
}
