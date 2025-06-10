package com.tripPlanner.project.entity.maps;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 관광지 : 하와이
    private String country; // 국가 : 미국
    private String description; // 미국의 인기있는 관광지로서...

    private String visa; // 비자 필요 여부
    private String flight; // 직항 - 4시간
    private String voltage; // 220V 110V
    private String timezone; // 시차 1시간

    private double latitude; // 위도(지도이동용)
    private double longitude; // 경도(지도이동용)

    private String imageUrl; // 간단한 이미지 1장
}
