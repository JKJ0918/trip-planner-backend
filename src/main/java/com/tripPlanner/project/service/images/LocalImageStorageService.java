package com.tripPlanner.project.service.images;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalImageStorageService implements ImageStorageService{

    private final String uploadDir = "uploads"; // 루트 디렉토리 기준


    @Override
    public String save(MultipartFile file) throws IOException {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dir = Paths.get(uploadDir);

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path filePath = dir.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 서버에서 접근 가능한 URL로 변경
        return "/static/uploads/" + fileName;
    }
}
