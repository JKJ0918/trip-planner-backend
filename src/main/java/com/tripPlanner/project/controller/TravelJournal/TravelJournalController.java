package com.tripPlanner.project.controller.TravelJournal;

import com.tripPlanner.project.dto.TravelJournal.TravelJournalRequestDTO;
import com.tripPlanner.project.dto.TravelJournal.TravelPostSummaryDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.TravelJournal.TravelJournalService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class TravelJournalController {

    private final TravelJournalService travelJournalService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // 여행일정 추가
    @PostMapping
    public ResponseEntity<?> save(@RequestBody TravelJournalRequestDTO request) throws IllegalAccessException {
        Long id = travelJournalService.saveTravelJournal(request);
        return ResponseEntity.ok(Map.of("journalId", id));
    }

    // 유저 정보 받아오기
    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request){

        String token = extractAccessToken(request);
        String name = jwtUtil.getUsername(token); // 토큰 상에는 사용자의 실명이 나타남
        String socialType = jwtUtil.getSocialType(token);

        UserEntity userEntity = userRepository.findByNameAndSocialType(name, socialType);

        String extId = userEntity.getId().toString();
        return ResponseEntity.ok(Map.of("userId", extId));

    }

   // 게시판 (여행일지 가져오기)
    @GetMapping("/public")
    public ResponseEntity<List<TravelPostSummaryDTO>> getPublicJournals() {
        List<TravelPostSummaryDTO> posts = travelJournalService.getPublicJournals();
        return ResponseEntity.ok(posts);
    }


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


}
