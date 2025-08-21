package com.tripPlanner.project.dto.travelJournal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TravelPostSummaryDTO {
    // 게시글 목록 관련

    private Long id; // 게시물 id
    private String title; // 제목
    private String locationSummary; // 여행지 적는 칸
    private String thumbnailUrl; // 썸네임 주소
    private String authorNickname; // 작성자 닉네임
    private LocalDateTime createdAt; //

    private long likeCount; // 좋아요 숫자
}
