package com.tripPlanner.project.controller.comments;

import com.tripPlanner.project.dto.comments.CommentRequestDTO;
import com.tripPlanner.project.dto.comments.CommentResponseDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.comments.CommentService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    
    // 댓글 작성
    @PostMapping("/{journalId}")
    public ResponseEntity<?> addComment(@PathVariable("journalId") Long journalId,
                                        @RequestBody CommentRequestDTO dto,
                                        HttpServletRequest request){

        Long userId = extractUserIdFromRequest(request);
        commentService.addComment(journalId, userId, dto);

        return ResponseEntity.ok().build();
    }

    // 댓글 불러오기
    @GetMapping("/{journalId}")
    public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable("journalId") Long journalId,
                                                                HttpServletRequest request){
        Long userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(commentService.getComments(journalId, userId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId,
                                           HttpServletRequest request){

        Long userId = extractUserIdFromRequest(request);
        commentService.deleteComment(commentId, userId);

        return ResponseEntity.ok().build();
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId,
                                           @RequestBody CommentRequestDTO dto,
                                           HttpServletRequest request){

        Long userId = extractUserIdFromRequest(request);
        commentService.updateComment(commentId, userId, dto);

        return ResponseEntity.ok().build();
    }

    // 좋아요 토클 api
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable("commentId") Long commentId, HttpServletRequest request){

        Long userId = extractUserIdFromRequest(request);

        boolean liked = commentService.toggleLike(commentId, userId);

        return ResponseEntity.ok(Map.of("liked", liked));
    }


    // 토큰 추출
    private String extractAccessToken(HttpServletRequest request){
        // 1. OAuth2 방식: 쿠키에서 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. JWT 방식: 헤더에서 찾기
        String header = request.getHeader("access");

        return null;
    }

    // 토큰에서 Long userId 추출
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String token = extractAccessToken(request);
        String name = jwtUtil.getUsername(token);
        String socialType = jwtUtil.getSocialType(token);
        UserEntity userEntity = userRepository.findByNameAndSocialType(name, socialType);
        return userEntity.getId();
    }


}
