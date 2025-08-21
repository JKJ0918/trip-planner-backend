package com.tripPlanner.project.controller.travelJournal;

import com.tripPlanner.project.dto.travelJournal.PostUpdateDTO;
import com.tripPlanner.project.dto.travelJournal.TravelJournalRequestDTO;
import com.tripPlanner.project.dto.travelJournal.TravelPostDetailDTO;
import com.tripPlanner.project.dto.travelJournal.TravelPostSummaryDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.travelJournal.TravelJournalService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class TravelJournalController {

    private final TravelJournalService travelJournalService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // 게시글 추가
    @PostMapping
    public ResponseEntity<?> save(@RequestBody TravelJournalRequestDTO request) throws IllegalAccessException {
        Long id = travelJournalService.saveTravelJournal(request);
        return ResponseEntity.ok(Map.of("journalId", id));
    }

    // 유저 정보 받아오기 - 컨트롤러
    @GetMapping("/auth/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            // 토큰/세션 없거나 만료 → 401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        return travelJournalService.getMe(userId) // 아래 서비스 메서드
                .<ResponseEntity<?>>map(me -> ResponseEntity.ok(me)) // 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "사용자를 찾을 수 없습니다.")));
    }


   // 게시글 리스트 (여행일지 가져오기) 페이지,
    @GetMapping("/public")
    public ResponseEntity<Page<TravelPostSummaryDTO>> getPublicJournals(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword
    ) {
        Page<TravelPostSummaryDTO> result = travelJournalService.getPublicJournals(page, size, keyword);
        return ResponseEntity.ok(result);
    }

    // 특정 게시물 가져오기 (상세 페이지, 게시글 수정)
    @GetMapping("/public/{id}")
    public TravelPostDetailDTO getPostDetails(@PathVariable("id") Long travelJournalId, HttpServletRequest request){

        Long userId = null;
        try {
            userId = extractUserIdFromRequest(request);
        }catch (Exception ignore){
            /* 비로그인/무효 토큰은 그냥 null 처리 */
        }
        return travelJournalService.getPostDetailById(travelJournalId, userId);
        // travelJournalId 는 게시글의 id를 뜻함. 좋아요 구현
    }

    /*
    // 코드보관 - 특정 게시물 가져오기 (상세 페이지, 게시글 수정)
    @GetMapping("/public/{id}")
    public TravelPostDetailDTO getPostDetails(@PathVariable("id") Long id){

        return travelJournalService.getPostDetailById(id);
    }
    */

    // 게시글 수정
    @PutMapping("/public/edit/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id,
                                        @RequestBody PostUpdateDTO dto,
                                        HttpServletRequest request) {

        Long userId = extractUserIdFromRequest(request);
        travelJournalService.updatePost(id, dto, userId);
        return ResponseEntity.ok("수정 완료");
    }

    // 게시글 삭제
    @DeleteMapping("/public/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id,
                                        HttpServletRequest request){

        System.out.println("삭제 되나요?");

        try {
            Long userId = extractUserIdFromRequest(request);
            travelJournalService.deletePost(id, userId);
            return ResponseEntity.ok().body("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제 실패" + e.getMessage());
        }


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
