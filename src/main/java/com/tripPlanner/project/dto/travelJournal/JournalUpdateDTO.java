package com.tripPlanner.project.dto.travelJournal;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class JournalUpdateDTO {

    private String title;
    private String content;
    private List<String> images;
    private LocalDate date; // 일별 일정 날짜

}
