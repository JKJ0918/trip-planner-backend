package com.tripPlanner.project.entity.TravelJournal;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
public class JournalEntity {
    // 일차별 여행일지
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private TravelJournalEntity travelJournalEntity;

    @OneToMany(mappedBy = "journalEntity", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PhotoEntity> photos = new ArrayList<>();

}
