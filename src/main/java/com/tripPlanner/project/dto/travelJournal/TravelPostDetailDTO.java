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
public class TravelPostDetailDTO {
    // 여행 상세정보
    private Long id;
    private String title;
    private String locationSummary;
    private String description;

    private Boolean useFlight;
    private String flightDepartureAirline;
    private String flightDepartureName;
    private String flightDepartureTime;
    private String flightDepartureAirport;
    private String flightArrivalAirport;
    private String flightReturnAirline;
    private String flightReturnName;
    private String flightReturnTime;
    private String flightReturnDepartureAirport;
    private String flightReturnArrivalAirport;
    private String travelTrans;
    private String totalBudget;
    private String travelTheme;
    private String review;
    private Boolean isAfterTravel;

    private DateRangeDTO dateRange;
    private String thumbnailUrl;
    private String authorNickname;
 
    
    private List<PinDTO> pins; // 핀정보
    private List<JournalReadDTO> itinerary; // 일일 일정


}
