package com.tripPlanner.project.component;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadStorage {

    @Value("${app.upload.root}")
    private String uploadRoot;               // 예: /var/app/uploads
    @Value("${app.upload.public-prefix:/uploads}")
    private String publicPrefix;             // 예: /uploads
    @Value("${app.upload.default-avatar:/uploads/basic_profile.png}")
    private String defaultAvatarPublicUrl;   // 예: /uploads/basic_profile.png

    // 로컬 업로드 URL 인지 확인
    public boolean isLocalPublicUrl(String publicUrl){
        if(publicUrl == null || publicUrl.isBlank()) {
            return false;
        }
        return publicUrl.startsWith(publicPrefix + "/");
    }

    //publicUrl에서 파일명만 추출
    public String extractFilename(String publicUrl){
        if(!isLocalPublicUrl(publicUrl)) {
            return null;
        }
        int idx = publicUrl.lastIndexOf('/');
        return (idx >= 0 && idx + 1 < publicUrl.length()) ? publicUrl.substring(idx + 1) : null;
    }

    // 루트 안의 안전한 경로로 변환(path traversal 방지)
    public Path resolveSafePath(String filename) {
        if(filename == null || filename.isBlank()) {
            return null;
        }
        Path root = Paths.get(uploadRoot).toAbsolutePath().normalize();
        Path target = root.resolve(filename).normalize();
        if(!target.startsWith(root)){
            throw new IllegalArgumentException("Invalid upload path outside root");
        }
        return target;
    }

    // 파일 1개 삭제(존재할 경우)
    public boolean deleteIfExists(Path path) {
        try {
            if(path ==null){
                return false;
            }
            boolean deleted = Files.deleteIfExists(path);
            if (deleted) {
                log.info("(삭제된 파일)Delted file: {}", path);
            } else {
                log.info("File not found for delete: {}", path);
            }
            return deleted;
        } catch (Exception e){
            log.warn("Failed to delete file: {} :: {}", path, e.toString());
            return false;
        }
    }

    /** // 썸네일 네이밍(s_ 접두어) 같이 지우고 싶다면 함께 처리 (아마 사용 안할 코드임)
    public void deleteOriginalAndThumbnail(String publicUrl) {
        if (!isLocalPublicUrl(publicUrl)) return;                           // 외부 URL 무시
        if (publicUrl.equals(defaultAvatarPublicUrl)) return;               // 기본 이미지 금지

        String filename = extractFilename(publicUrl);
        if (filename == null) return;

        // 원본
        Path original = resolveSafePath(filename);
        deleteIfExists(original);

        // (옵션) 썸네일: 예) s_ 접두어로 저장하는 정책을 쓰는 경우
        // 저장 정책에 맞춰 사용하세요. UUID.ext만 쓰면 아래는 불필요합니다.
        String thumbName = "s_" + filename;
        Path thumb = resolveSafePath(thumbName);
        deleteIfExists(thumb);
    }
     */


}
