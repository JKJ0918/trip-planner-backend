package com.tripPlanner.project.dto.travelJournal;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


}
