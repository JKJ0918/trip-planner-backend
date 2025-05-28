package com.tripPlanner.project.dto.Maps;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LocationDTO {
    private Long id;
    private String name;
    private String country;
    private String description;
    private String visa;
    private String flight;
    private String voltage;
    private String timezone;
    private double latitude;
    private double longitude;
    private String imageUrl;

    public LocationDTO(Long id, String name, String country, String description, String visa, String flight, String voltage, String timezone, double latitude, double longitude, String imageUrl) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.description =description;
        this.visa = visa;
        this.flight = flight;
        this.voltage = voltage;
        this.timezone = timezone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
    }

}
