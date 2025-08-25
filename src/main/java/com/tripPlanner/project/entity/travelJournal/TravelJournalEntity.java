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

    private String title; // 게시글 제목
    private String locationSummary; // 여행 도시
    private String description; // 내용
    private boolean isPublic; // 게시글 공개 여부

    private Boolean useFlight; // 항공기 탑승 여부

    private String flightDepartureAirline;
    private String flightDepartureName;
    private String flightDepartureTime;
    private String flightDepartureAirport; // 출국편 출발공항
    private String flightArrivalAirport; // 출국편 도착공항
    private String flightReturnAirline;
    private String flightReturnName;
    private String flightReturnTime;
    private String flightReturnDepartureAirport; // 귀국편 출발 공항
    private String flightReturnArrivalAirport; // 귀국편 도착 공항

    private String travelTrans;
    private String totalBudget;
    private String travelTheme;
    private String review;
    private Boolean isAfterTravel;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false) // 조회수
    private Long views = 0L;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성시간 체크용

    @OneToMany(mappedBy = "travelJournalEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JournalEntity> journalEntities = new ArrayList<>(); // 일일 일정

    @OneToMany(mappedBy = "travelJournalPinEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PinEntity> pinEntities = new ArrayList<>(); // 핀 리스트

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    @OneToMany(mappedBy = "travelJournalLikeEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<JournalLikeEntity> likes = new ArrayList<>();





}

