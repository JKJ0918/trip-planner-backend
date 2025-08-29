package com.tripPlanner.project.controller.notification;

import com.tripPlanner.project.dto.notification.NotificationDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.repository.notification.NotificationRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository repo;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public NotificationController(NotificationRepository repo, JWTUtil jwtUtil, UserRepository userRepository) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Page<NotificationDTO> list(
            HttpServletRequest req,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Long userId = extractUserIdFromRequest(req);
        System.out.println("public Page<NotificationDTO> list( 체크 :" + userId);
        return repo.findByRecipientIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(NotificationDTO::from);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(HttpServletRequest req) {
        Long userId = extractUserIdFromRequest(req);
        System.out.println("public Map<String, Long> unreadCount(HttpServletRequest req) { 체크 : " + userId);
        long count = repo.countByRecipientIdAndIsReadFalse(userId);
        return Map.of("count", count);
    }

    @PostMapping("/{id}/read")
    public void markRead(HttpServletRequest req, @PathVariable("id") Long id) {
        Long userId = extractUserIdFromRequest(req);
        int updated = repo.markRead(id, userId);
        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 알림이 없거나 권한이 없습니다.");
    }

    @PostMapping("/read-all")
    public void markAllRead(HttpServletRequest req) {
        Long userId = extractUserIdFromRequest(req);
        repo.markAllRead(userId);
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
