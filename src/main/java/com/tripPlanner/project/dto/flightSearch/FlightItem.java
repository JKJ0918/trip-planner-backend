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
    private String airline;

    @XmlElement(name = "airport")
    private String departure;

    @XmlElement(name = "city")
    private String arrival;

    @XmlElement(name = "internationalTime")
    private String departureTime;

    @XmlElement(name = "internationalNum")
    private String airPlanecode;



}
