package com.tripPlanner.project.dto.flightSearch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class FlightItem {
    // 항공편 1건의 정보를 담는 Java 클래스(DTO)

    @XmlElement(name = "airlineKorean")
    private String airline; // 항공사

    @XmlElement(name = "airport")
    private String departure; // 국내 공항

    @XmlElement(name = "city")
    private String arrival; // 목적지 도시

    @XmlElement(name = "internationalTime")
    private String departureTime; // 국내공항에서 출발시간, 국내공항으로 도착 시간

    @XmlElement(name = "internationalNum")
    private String airPlanecode;

    @XmlElement(name = "internationalIoType")
    private String inout; // IN : 목적지 도시 -> 국내 공항
                          // OUT : 국내 공항 -> 목적지 도시



}
