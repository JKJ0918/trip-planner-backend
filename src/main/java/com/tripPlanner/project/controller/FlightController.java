package com.tripPlanner.project.controller;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightSearchDTO;
import com.tripPlanner.project.service.FlightSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Controller
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchServic){

        this.flightSearchService = flightSearchServic;
    }

    // 구현중 : api를 통한 항공권 검색
    @PostMapping("/api/flights")
    public ResponseEntity<?> searchFlights(@RequestBody FlightSearchDTO flightSearchDTO){

        Map<String, List<FlightItem>> result = flightSearchService.searchRoundTrip(flightSearchDTO);
        return ResponseEntity.ok(result);

    }
}
