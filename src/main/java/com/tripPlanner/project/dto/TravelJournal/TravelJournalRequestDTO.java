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
public class TravelJournalRequestDTO {
    // 여행일지 총괄
    private String startDate; // 시작날
    private String endDate; // 종료날
    private String userId; // 작성자 Id

    private List<PinDTO> pins; // pin(구글맵 위치) 정보 리스트
    private List<JournalDTO> journals; // 일차별 리스트

}
