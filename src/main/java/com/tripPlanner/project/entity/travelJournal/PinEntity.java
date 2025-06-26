package com.tripPlanner.project.entity.travelJournal;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    // 이미지 URL을 리스트로 저장 (DB에 별도 테이블 생성)
    @ElementCollection
    @CollectionTable(name = "pin_images", joinColumns = @JoinColumn(name = "pin_id"))
    @Column(name = "image_url")
    private List<String> images;

    private String minCost;
    private String maxCost;
    private String currency;
    private String openTime;
    private String closeTime;
    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private TravelJournalEntity travelJournalPinEntity;
}
