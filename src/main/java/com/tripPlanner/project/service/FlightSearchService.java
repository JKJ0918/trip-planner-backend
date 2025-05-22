package com.tripPlanner.project.service;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightSearchDTO;
import com.tripPlanner.project.flights.FlightApiClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlightSearchService {

    private final FlightApiClient flightApiClient;

    public FlightSearchService(FlightApiClient flightApiClient){

        this.flightApiClient = flightApiClient;
    }

    public Map<String, List<FlightItem>> searchRoundTrip(FlightSearchDTO flightSearchDTO) {
        List<FlightItem> goList = flightApiClient.fetchFlights(flightSearchDTO.getDeparture(), flightSearchDTO.getArrival(), flightSearchDTO.getDepartureDate());
        List<FlightItem> returnList = flightApiClient.fetchFlights(flightSearchDTO.getArrival(), flightSearchDTO.getDeparture(), flightSearchDTO.getReturnDate());

        Map<String, List<FlightItem>> result = new HashMap<>();
        result.put("go", goList);
        result.put("back", returnList);
        return result;
    }

}
