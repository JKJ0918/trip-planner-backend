package com.tripPlanner.project.entity.travelJournal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "journal_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"travelJournal_id", "user_id"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JournalLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글과 다:1
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travelJournal_id", nullable = false)
    private TravelJournalEntity travelJournalLikeEntity;

    // 유저는 Long만 저장
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist(){
        if (createdAt == null){
            createdAt = LocalDateTime.now();
        }
    }

}
