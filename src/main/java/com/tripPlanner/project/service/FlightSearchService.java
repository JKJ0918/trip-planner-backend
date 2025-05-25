package com.tripPlanner.project.service;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import com.tripPlanner.project.dto.flightSearch.FlightSearchDTO;
import com.tripPlanner.project.flights.FlightApiClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightSearchService {

    private final FlightApiClient flightApiClient;

    public FlightSearchService(FlightApiClient flightApiClient){

        this.flightApiClient = flightApiClient;
    }

    public FlightResponseDTO getGoFlights(FlightSearchDTO flightSearchDTO) {
        return flightApiClient.fetchFlights(
                flightSearchDTO.getDeparture(),
                flightSearchDTO.getArrival(),
                flightSearchDTO.getDepartureDate(),
                flightSearchDTO.getGoPage()
        );
    }



}
