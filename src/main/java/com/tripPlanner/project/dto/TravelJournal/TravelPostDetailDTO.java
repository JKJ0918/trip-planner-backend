package com.tripPlanner.project.dto.TravelJournal;

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

    private Long id;
    private String title;
    private String locationSummary;
    private DateRangeDTO dateRange;
    private String thumbnailUrl;
    private String authorNickname;
    private List<PinDTO> pins;
    private List<ItineraryDTO> itinerary;


}
