package com.tripPlanner.project.service.myPage;

import com.tripPlanner.project.component.UploadStorage;
import com.tripPlanner.project.dto.myPage.MeDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;
    private final UploadStorage uploadStorage;

    @Transactional(readOnly = true)
    public MeDTO.MeResponse getMe(Long userId) {
        UserEntity u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return toMeResponse(u);
    }

    @Transactional(readOnly = false)
    public MeDTO.MeResponse updateMe(Long userId, MeDTO.UpdateMeRequest req) {
        UserEntity u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 변경 전 URL 백업
        String oldUrl = u.getAvatarUrl();

        // 닉네임 업데이트 (null/빈값 방지 필요 시 검증 추가)
        if (req.nickname() != null && !req.nickname().isBlank()) {
            u.setNickname(req.nickname().trim());
        }

        // avatarUrl 규칙:
        // - req.avatarUrl() == null  → 아바타를 변경하지 않음(그대로 둠)
        // - req.avatarUrl() == ""    → 기본 이미지로 초기화(= DB에는 null 저장 권장)
        // - 그 외(유효 문자열)          → 해당 URL로 업데이트
        String incoming = req.avatarUrl();
        if (incoming != null) {
            if (incoming.isBlank()) {
                // 기본 이미지로 초기화 → DB에는 null 저장(프론트에서 기본 이미지 표시 or 서버에서 default로 치환)
                u.setAvatarUrl(null);
            } else {
                u.setAvatarUrl(incoming);
            }
        }

        // 삭제 필요 여부 판단 (old != null && old != new && 로컬 업로드 url && 기본 이미지 아님)
        String newUrl = u.getAvatarUrl();
        boolean shouldDelete =
            oldUrl != null && // 기존 이미지가 있고
            !oldUrl.isBlank() && // 기존 이미지가 있고
            !oldUrl.equals(newUrl) && // 새 이미지 이름과 다르고
            uploadStorage.isLocalPublicUrl(oldUrl); // + 기본 이미지는 내부에서 차단됨

        if (shouldDelete) {
            // 트랜잭션 커밋 이후 파일 삭제 (롤백 대비)

            String filename = uploadStorage.extractFilename(oldUrl);
            Path original = uploadStorage.resolveSafePath(filename);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    uploadStorage.deleteIfExists(original); // ← 여기서 퍼사드 호출
                }
            });
        }

        // JPA dirty checking으로 반영됨
        return toMeResponse(u);
    }


    private MeDTO.MeResponse toMeResponse(UserEntity u) {
        return new MeDTO.MeResponse(
                u.getId(),
                u.getNickname(),
                u.getEmail(),
                u.getAvatarUrl() // null이면 프론트에서 기본 이미지로 표시
        );
    }
}
