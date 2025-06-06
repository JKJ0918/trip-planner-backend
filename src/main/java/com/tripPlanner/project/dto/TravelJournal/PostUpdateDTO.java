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
    private String locationSummary;
    private DateRangeDTO dateRange;
    private List<PinDTO> pins;
    private List<JournalUpdateDTO> journalUpdate; // 일일 일정
}
