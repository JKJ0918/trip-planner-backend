package com.tripPlanner.project.controller.Maps;

import com.tripPlanner.project.dto.Maps.LocationDTO;
import com.tripPlanner.project.repository.Maps.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController // (Controller + ResponseBody)
@RequestMapping("/api/locations")
@RequiredArgsConstructor // final 필드들을 자동으로 생성자 주입
public class LocationController {

    private final LocationRepository locationRepository;


    @GetMapping
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationEntity -> new LocationDTO(
                        locationEntity.getId(),
                        locationEntity.getName(),
                        locationEntity.getCountry(),
                        locationEntity.getDescription(),
                        locationEntity.getVisa(),
                        locationEntity.getFlight(),
                        locationEntity.getVoltage(),
                        locationEntity.getTimezone(),
                        locationEntity.getLatitude(),
                        locationEntity.getLongitude(),
                        locationEntity.getImageUrl()
                ))
                .collect(Collectors.toList());
    }
}
