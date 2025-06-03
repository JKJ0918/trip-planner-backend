package com.tripPlanner.project.dto.TravelJournal;

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

    private Long id;
    private String title;
    private String locationSummary;
    private String thumbnailUrl;
    private String authorNickname;
    private LocalDateTime createdAt;
}
