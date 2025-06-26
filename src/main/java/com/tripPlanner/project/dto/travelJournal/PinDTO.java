package com.tripPlanner.project.dto.travelJournal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor // 기본 생성자 자동 추가
@AllArgsConstructor
//@Builder
public class PinDTO {

    private double lat;
    private double lng;
    private String name;
    private String category;
    private String address;
    private List<String> images; // pin 이미지 저장

    private String minCost;
    private String maxCost;
    private String currency;
    private String openTime;
    private String closeTime;
    private String description;


}
