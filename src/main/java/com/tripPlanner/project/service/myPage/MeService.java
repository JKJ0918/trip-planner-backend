package com.tripPlanner.project.service.myPage;

import com.tripPlanner.project.dto.myPage.MeDTO;
import com.tripPlanner.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;

    public MeDTO.MeResponse getMe(Long userId) {
        var u = userRepository.findById(userId).orElseThrow();
        return new MeDTO.MeResponse(u.getId(), u.getNickname(), u.getEmail());
    }

    public void updateNickname(Long userId, String nickname) {
        // 닉네임 유효성/중복 검사 등
        var u = userRepository.findById(userId).orElseThrow();
        u.setNickname(nickname);
        userRepository.save(u);
    }
}
