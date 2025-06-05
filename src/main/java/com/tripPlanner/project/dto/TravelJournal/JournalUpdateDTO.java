package com.tripPlanner.project.dto.TravelJournal;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JournalUpdateDTO {

    private LocalDate date;             // 날짜별 구분
    private String entryTitle;
    private String entryDescription;
    private List<String> imageUrls;     // 이미지는 URL 기반 수정

}
