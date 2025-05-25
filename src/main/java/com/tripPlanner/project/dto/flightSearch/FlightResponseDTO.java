package com.tripPlanner.project.dto.flightSearch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class FlightResponseDTO {

    @XmlElement(name = "body")
    private Body body;

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "items")
        private Items items;

        @XmlElement(name = "numOfRows")
        private int numOfRows;

        @XmlElement(name = "pageNo")
        private int pageNo;

        @XmlElement(name = "totalCount")
        private int totalCount;
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
        return List.of(); // null 방지
    }

    public int getTotalCount() {
        if (body != null) {
            return body.totalCount;
        }
        return 0;
    }
}
