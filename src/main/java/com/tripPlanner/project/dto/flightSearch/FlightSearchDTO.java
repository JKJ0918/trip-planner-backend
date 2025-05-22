package com.tripPlanner.project.dto.flightSearch;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlightSearchDTO {

    private String departure;       // 예: "ICN"
    private String arrival;         // 예: "KIX"
    private String departureDate;   // 예: "20240525"
    private String returnDate;      // 예: "20240625"

}
