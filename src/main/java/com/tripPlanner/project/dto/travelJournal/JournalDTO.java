package com.tripPlanner.project.dto.travelJournal;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JournalDTO {
    // 일차별 세부 일정 작성 저장을 위해 받는 부분
    private String date; // ISO format: "2024-06-01"
    private String title;
    private String description;
    private List<String> photos; // 이미지 URL 목록

}
