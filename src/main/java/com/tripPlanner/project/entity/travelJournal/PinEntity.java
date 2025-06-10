package com.tripPlanner.project.entity.travelJournal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private TravelJournalEntity travelJournalPinEntity;
    // travelJournalRntity_id 외래키 fk 주인 설정
}
