package com.tripPlanner.project.service.flights;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import com.tripPlanner.project.dto.flightSearch.FlightSearchDTO;
import com.tripPlanner.project.flights.FlightApiClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


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

    public FlightResponseDTO getBackFlights(FlightSearchDTO flightSearchDTO) {
        return flightApiClient.fetchFlights(
                flightSearchDTO.getArrival(),
                flightSearchDTO.getDeparture(),
                flightSearchDTO.getReturnDate(),
                flightSearchDTO.getBackPage()
        );
    }

    public List<FlightItem> removeDuplicateFlights(List<FlightItem> flights) {
        return flights.stream()
                .collect(Collectors.toMap(
                        flight -> flight.getAirline() + flight.getAirPlanecode() + flight.getDepartureTime(), // 중복 기준
                        Function.identity(), // 동일한 키가 나오면 첫 번째 값 유지
                        (existing, duplicate) -> existing // 충돌 시 기존 값 유지
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }



}
