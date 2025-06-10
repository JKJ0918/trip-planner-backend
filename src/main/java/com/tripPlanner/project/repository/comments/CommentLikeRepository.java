package com.tripPlanner.project.repository.comments;

import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.entity.comments.CommentEntity;
import com.tripPlanner.project.entity.comments.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {

    // 특정 댓글에 특정 사용자가 좋아요를 눌렀는지 확인
    Optional<CommentLikeEntity> findByUserAndComment(UserEntity user, CommentEntity comment);

    // 특정 댓글에 대한 좋아요 수 조회 (DTO에 사용)
    int countByComment(CommentEntity comment);

    // 댓글 목록 조회 시 좋아요 여부를 한 번에 조회하고 싶다면 (선택)
    List<CommentLikeEntity> findByUserAndCommentIn(UserEntity user, List<CommentEntity> comments);

}
