package com.tripPlanner.project.service.images;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    String save(MultipartFile file) throws IOException;
    void delete(String imageUrl) throws IOException; // 삭제 메서드
}
