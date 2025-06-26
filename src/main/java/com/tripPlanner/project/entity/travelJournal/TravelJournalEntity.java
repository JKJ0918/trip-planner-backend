package com.tripPlanner.project.entity.travelJournal;

import com.tripPlanner.project.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelJournalEntity {

 // 여행일지 작성 (게시글 작성)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private String title; // 게시글 제목
    private String locationSummary; // 여행 도시
    private boolean isPublic; // 게시글 공개 여부

    private Boolean useFlight; // 항공기 탑승 여부

    private String flightDepartureAirline;
    private String flightDepartureName;
    private String flightDepartureTime;
    private String flightReturnAirline;
    private String flightReturnName;
    private String flightReturnTime;

    private String travelTrans;
    private String totalBudget;
    private String travelTheme;
    private String review;
    private Boolean isAfterTravel;


    private LocalDate startDate;
    private LocalDate endDate;


    @OneToMany(mappedBy = "travelJournalPinEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PinEntity> pinEntities = new ArrayList<>();

    @OneToMany(mappedBy = "travelJournalEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JournalEntity> journalEntities = new ArrayList<>(); // 일일 일정

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성시간 체크용



}

