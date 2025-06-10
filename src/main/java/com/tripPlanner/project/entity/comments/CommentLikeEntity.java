package com.tripPlanner.project.entity.comments;

import com.tripPlanner.project.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class CommentLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommentEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    private LocalDateTime likedAt = LocalDateTime.now();
}
