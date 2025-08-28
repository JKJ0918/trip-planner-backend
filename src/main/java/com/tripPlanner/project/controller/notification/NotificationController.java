package com.tripPlanner.project.controller.notification;

import com.tripPlanner.project.dto.notification.NotificationDTO;
import com.tripPlanner.project.repository.notification.NotificationRepository;
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

    public NotificationController(NotificationRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<NotificationDTO> list(
            HttpServletRequest req,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Long userId = getUserId(req);
        return repo.findByRecipientIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(NotificationDTO::from);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(HttpServletRequest req) {
        Long userId = getUserId(req);
        long count = repo.countByRecipientIdAndIsReadFalse(userId);
        return Map.of("count", count);
    }

    @PostMapping("/{id}/read")
    public void markRead(HttpServletRequest req, @PathVariable("id") Long id) {
        Long userId = getUserId(req);
        int updated = repo.markRead(id, userId);
        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 알림이 없거나 권한이 없습니다.");
    }

    @PostMapping("/read-all")
    public void markAllRead(HttpServletRequest req) {
        Long userId = getUserId(req);
        repo.markAllRead(userId);
    }

    private Long getUserId(HttpServletRequest req) {
        Object v = req.getAttribute("userId");
        if (v instanceof Long l) return l;
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
    }



}
