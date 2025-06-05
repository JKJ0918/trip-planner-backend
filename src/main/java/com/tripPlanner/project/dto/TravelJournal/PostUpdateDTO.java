package com.tripPlanner.project.dto.TravelJournal;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PostUpdateDTO {
    // 게시글 수정
    private String title;
    private String locationSummary; // 여행 방문지들 시드니, 오사카, 도쿄
    private LocalDate startDate;
    private LocalDate endDate;
    private List<PinDTO> pins;
    private List<JournalUpdateDTO> journalUpdate; // 일일 일정
}
