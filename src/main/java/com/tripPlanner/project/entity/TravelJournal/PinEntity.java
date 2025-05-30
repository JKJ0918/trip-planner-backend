package com.tripPlanner.project.entity.TravelJournal;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
public class PinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double lat;
    private double lng;
    private String name;
    private String category;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private TravelJournalEntity travelJournalEntity;

}
