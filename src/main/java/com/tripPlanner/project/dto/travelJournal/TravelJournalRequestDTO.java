package com.tripPlanner.project.dto.travelJournal;

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
    // 작성한 여행일지 저장
    private String startDate; // 시작날
    private String endDate; // 종료날
    private String userId; // 작성자 Id
    private String title; // 게시글 제목
    private String locationSummary; // 여행 도시
    private Boolean isPublic; // 게시글 공개 여부
    // boolean isPublic 필드는 Java Bean 규칙상 getter가 isIsPublic()으로 생성 -> Boolean 으로 변경

    private List<PinDTO> pins; // pin(구글맵 위치) 정보 리스트
    private List<JournalDTO> journals; // 일차별 리스트

}
