package com.tripPlanner.project.entity.TravelJournal;

import com.tripPlanner.project.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
public class TravelJournalEntity {

 // 여행일지 작성 (게시글 작성)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "travelJournalEntity", cascade = CascadeType.ALL)
    private List<PinEntity> pinEntities = new ArrayList<>();

    @OneToMany(mappedBy = "travelJournalEntity", cascade = CascadeType.ALL)
    private List<JournalEntity> journalEntities = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

}

