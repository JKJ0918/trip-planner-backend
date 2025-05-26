package com.tripPlanner.project.controller;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import com.tripPlanner.project.dto.flightSearch.FlightSearchDTO;
import com.tripPlanner.project.service.flights.FlightSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FlightController {

    private final FlightSearchService flightSearchService;

    public FlightController(FlightSearchService flightSearchService){

        this.flightSearchService = flightSearchService;
    }

    // 구현중 : api를 통한 항공권 검색
    @PostMapping("/api/flights")
    public ResponseEntity<?> searchFlights(@RequestBody FlightSearchDTO flightSearchDTO){

        FlightResponseDTO go = flightSearchService.getGoFlights(flightSearchDTO);
        FlightResponseDTO back = flightSearchService.getBackFlights(flightSearchDTO);

        List<FlightItem> goItems = go.getItems();
        List<FlightItem> backItems = back.getItems();

        // 중복 제거
        goItems = flightSearchService.removeDuplicateFlights(goItems);
        backItems = flightSearchService.removeDuplicateFlights(backItems);


        int size = flightSearchDTO.getSize();

        int goPage = flightSearchDTO.getGoPage();
        int backPage = flightSearchDTO.getBackPage();

        int totalGo = go.getTotalCount();
        int totalBack = go.getTotalCount();


        boolean hasMoreGo = goPage < (int) Math.ceil((double) totalGo / size);
        boolean hasMoreBack = backPage < (int) Math.ceil((double) totalBack / size);

        Map<String, Object> goResult = new HashMap<>();
        goResult.put("items", goItems);
        goResult.put("nextPage", hasMoreGo ? goPage + 1 : null);
        goResult.put("hasNext", hasMoreGo);

        Map<String, Object> backResult = new HashMap<>();
        backResult.put("items", backItems);
        backResult.put("nextPage", hasMoreBack ? backPage + 1 : null);
        backResult.put("hasNext", hasMoreBack);

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("go", goResult);
        finalResult.put("back", backResult);


        return ResponseEntity.ok(finalResult);

    }


}
