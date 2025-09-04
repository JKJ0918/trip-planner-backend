package com.tripPlanner.project.dto.myPage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class MyJournalsDTO {
    // 본인 게시글 불러오기
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private long likeCount; // 좋아요 숫자
    private long views; // 게시글 조회수
}
