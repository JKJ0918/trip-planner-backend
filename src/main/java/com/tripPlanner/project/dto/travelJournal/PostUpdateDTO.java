package com.tripPlanner.project.dto.travelJournal;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateDTO {
    // 게시글 수정
    private String title;
    private String locationSummary;
    private DateRangeDTO dateRange;
    private List<PinDTO> pins;
    private List<JournalUpdateDTO> itinerary; // 일일 일정 *프론트의 JSON 타입과 맞춰줌
}
