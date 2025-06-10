package com.tripPlanner.project.dto.comments;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {
    // 댓글 응답 DTO

    private Long id;
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private Long parentId; // 대댓글
    private boolean edited; // 수정
    
    private int likeCount; // 좋아요 수
    private boolean likedByMe; // 내가 누른 좋아요 인지
}
