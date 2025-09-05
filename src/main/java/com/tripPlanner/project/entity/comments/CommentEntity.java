package com.tripPlanner.project.entity.comments;

import com.tripPlanner.project.entity.travelJournal.TravelJournalEntity;
import com.tripPlanner.project.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_id")
    private TravelJournalEntity travelJournal;

    // 대댓글 - (부모 객체)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    // 대댓글 - (자식 객체, 읽기용, 저장은 parent가 주도)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> children = new ArrayList<>();

    // 댓글 수정
    @Column(nullable = false)
    private boolean edited = false;

    // 좋아요
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLikeEntity> likes = new ArrayList<>();




}
