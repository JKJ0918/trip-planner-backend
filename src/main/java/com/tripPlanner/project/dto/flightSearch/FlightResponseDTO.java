package com.tripPlanner.project.dto.flightSearch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@XmlRootElement(name ="response")
@XmlAccessorType(XmlAccessType.FIELD)
public class FlightResponseDTO {

    // 공항 데이터 구조  <response> → <body> → <items> → <item> 구조
    // 파싱 -> wrapper 객체 필요

    @XmlElement(name = "body")
    private Body body;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "items")
        private Items items;
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Items {
        @XmlElement(name = "item")
        private List<FlightItem> itemList;
    }

    public List<FlightItem> getItems() {
        if (body != null && body.items != null) {
            return body.items.itemList;
        }
        return null;
    }

}
