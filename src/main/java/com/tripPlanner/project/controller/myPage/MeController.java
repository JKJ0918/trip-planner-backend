package com.tripPlanner.project.controller.myPage;

import com.tripPlanner.project.dto.myPage.MeDTO;
import com.tripPlanner.project.dto.myPage.MyJournalsDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.myPage.MeService;
import com.tripPlanner.project.service.myPage.MyJournalsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MeController {
    private final MeService meService;
    private final MyJournalsService myJournalsService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    // useMe
    @GetMapping("/auth/me")
    public ResponseEntity<?> getMe(HttpServletRequest request){
        Long userId = extractUserIdFromRequest(request);
        if(userId == null){
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        return ResponseEntity.ok(meService.getMe(userId));
    }

    // 프로필 닉네임/프로필 사진 수정
    @PutMapping("/users/me")
    public ResponseEntity<?> updateMe(HttpServletRequest request, @RequestBody MeDTO.UpdateMeRequest body) {
        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        MeDTO.MeResponse updated = meService.updateMe(userId, body);

        return ResponseEntity.ok(updated);
    }

    // 내 여행 목록 가져오기
    @GetMapping("/me/journals")
    public ResponseEntity<?> getMyJournals (
            HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "12") int size
    ) {
        Long userId = extractUserIdFromRequest(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        Page<MyJournalsDTO> result = myJournalsService.getMyJournals(userId, page, size);

        // 래핑 형식으로 응답
        Map<String, Object> body = Map.of(
                "items", result.getContent(),
                "total", result.getTotalElements(),
                "page", page,
                "size", size
        );
        return ResponseEntity.ok(body);


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

        // 토큰 없을 시 null 반환
        if (!org.springframework.util.StringUtils.hasText(token)) {
            return null;
        }

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
