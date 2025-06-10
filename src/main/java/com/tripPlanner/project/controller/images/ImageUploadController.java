package com.tripPlanner.project.controller.images;

import com.tripPlanner.project.service.images.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor // final 필드들을 자동으로 생성자 주입
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestPart("file")MultipartFile file){

        try {
            String imageUrl = imageStorageService.save(file);
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    // 게시글 수정 이미지 업로드
    @PostMapping("/edit/upload")
    public ResponseEntity<Map<String, List<String>>> uploadMultipleImages(@RequestPart("files") MultipartFile[] files) {
        try {
            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = imageStorageService.save(file);
                urls.add(imageUrl);
            }

            Map<String, List<String>> response = new HashMap<>();
            response.put("imageUrls", urls);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", List.of(e.getMessage())));
        }
    }


    // 게시글 수정 이미지 삭제 요청
    @PostMapping("/edit/delete")
    public ResponseEntity<String> deleteImages(@RequestBody Map<String, List<String>> payload) {
        List<String> imageUrls = payload.get("imageUrls");
        for (String url : imageUrls) {
            try {
                imageStorageService.delete(url); // 아래에 메서드 추가해야 함
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
            }
        }
        return ResponseEntity.ok("삭제 완료");
    }
}
