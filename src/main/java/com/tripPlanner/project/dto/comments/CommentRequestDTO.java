package com.tripPlanner.project.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    // 댓글 요청 DTO
    private String content;
    
    private Long parentId; // null : 일반, !null : 대댓글

}
