package com.tripPlanner.project.dto.TravelJournal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JournalReadDTO {
    // 상세보기 여행 일일 일정을 받아주는 부분
    // frontend - posts/components/PostItinerary.tsx item.~ 와 관련됨
    private int day;
    private String title;   // 일정 제목
    private String content; // 일정 상세 내용 
    private List<String> images; // 이미지 URL 목록

}
