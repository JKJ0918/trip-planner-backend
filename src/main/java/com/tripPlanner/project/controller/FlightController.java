package com.tripPlanner.project.controller;

import com.tripPlanner.project.dto.flightSearch.FlightItem;
import com.tripPlanner.project.dto.flightSearch.FlightResponseDTO;
import com.tripPlanner.project.dto.flightSearch.FlightSearchDTO;
import com.tripPlanner.project.service.FlightSearchService;
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

    public FlightController(FlightSearchService flightSearchServic){

        this.flightSearchService = flightSearchServic;
    }

    // 구현중 : api를 통한 항공권 검색
    @PostMapping("/api/flights")
    public ResponseEntity<?> searchFlights(@RequestBody FlightSearchDTO flightSearchDTO){

        FlightResponseDTO go = flightSearchService.getGoFlights(flightSearchDTO);
        // List<FlightItem> backList = flightSearchService.getBackFlights(flightSearchDTO);
        List<FlightItem> goItems = go.getItems();
        int size = flightSearchDTO.getSize();

        int goPage = flightSearchDTO.getGoPage();
        int backPage = flightSearchDTO.getBackPage();
        int totalGo = go.getTotalCount();
        // int totalBackPages = (int) Math.ceil((double) backList.size() / size);


        // List<FlightItem> paginatedBack = flightSearchService.paginate(backList, backPage, size);

        boolean hasMoreGo = goPage < (int) Math.ceil((double) totalGo / size);
        // boolean hasMoreBack = backPage < totalBackPages;

        Map<String, Object> goResult = new HashMap<>();
        goResult.put("items", goItems);
        goResult.put("nextPage", hasMoreGo ? goPage + 1 : null);
        goResult.put("hasNext", hasMoreGo);

       /* Map<String, Object> backResult = new HashMap<>();
        backResult.put("items", paginatedBack);
        backResult.put("nextPage", hasMoreBack ? backPage + 1 : null);
        backResult.put("hasNext", hasMoreBack);*/

        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("go", goResult);
        //finalResult.put("back", backResult);


        return ResponseEntity.ok(finalResult);

    }
}
