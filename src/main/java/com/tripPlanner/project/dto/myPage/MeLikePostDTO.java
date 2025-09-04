package com.tripPlanner.project.dto.myPage;

public record MeLikePostDTO(
        Long id,
        String title,
        String nickname,
        java.time.LocalDateTime createdAt// 게시물 작성일

) {}
