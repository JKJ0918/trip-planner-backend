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

    private Long id; // 댓글 id
    private String content;
    private String writerName;
    private LocalDateTime createdAt;
    private Long parentId; // 대댓글
    private int replyCount; // 대 댓글 수
    private boolean edited; // 수정
    
    private boolean isAuthor; // 로그인 유저가 작성자인지 여부
    
    private int likeCount; // 좋아요 수
    private boolean likedByMe; // 내가 누른 좋아요 인지
}
