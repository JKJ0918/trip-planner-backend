package com.tripPlanner.project.controller.travelJournal;

import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.travelJournal.JournalLikeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TravelJournalLikeController {

    private final JournalLikeService journalLikeService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // 좋아요
    @PutMapping("/api/journals/{id}/like")
    public ResponseEntity<Void> like(@PathVariable("id") Long id, HttpServletRequest request){
        Long userId = extractUserIdFromRequest(request);
        journalLikeService.like(id, userId);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 취소
    @DeleteMapping("/api/journals/{id}/like")
    public ResponseEntity<Void> unlike(@PathVariable("id") Long id, HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        journalLikeService.unlike(id, userId);
        return ResponseEntity.noContent().build();
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

        UserEntity user = new UserEntity();
        if(socialType.equals("localUser")){
            user = userRepository.findByUsernameAndSocialType(name, "localUser");
        }else {
            user = userRepository.findByNameAndSocialType(name, socialType);
        }

        return user.getId();
    }

}
