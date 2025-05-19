package com.tripPlanner.project.controller;

import com.tripPlanner.project.dto.JoinDTO;
import com.tripPlanner.project.dto.SocialJoinDTO;
import com.tripPlanner.project.entity.UserEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.UserRepository;
import com.tripPlanner.project.service.JoinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    public JoinController(JoinService joinService, UserRepository userRepository, JWTUtil jwtUtil){

        this.joinService = joinService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // 일반 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO joinDTO) {

        // 중복 검사
        if (userRepository.existsByUsername(joinDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByNickname(joinDTO.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 닉네임입니다.");
        }

        joinService.joinProcess(joinDTO);
        
        return ResponseEntity.ok("회원가입 완료");

    }



    //SocialJoin additional-information
    @PostMapping("/api/user/additional-info")
    private ResponseEntity<?> saveadditionalInfo(@RequestBody SocialJoinDTO socialJoinDTO, HttpServletRequest request){

        // 사용자 인증 - 쿠키에서 토큰 추출
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not found");
        }

        String tokenUsername = jwtUtil.getUsername(token);
        UserEntity user = userRepository.findByName(tokenUsername);
        UserEntity currentUser = userRepository.findByUsername(user.getUsername());

        // 닉네임 중복 사용자 조회
        Optional<UserEntity> nicknameUserOpt = userRepository.findByNickname(socialJoinDTO.getNickname());


        if (nicknameUserOpt.isPresent()) {
            UserEntity nicknameUser = nicknameUserOpt.get();

            // 현재 로그인한 유저가 아닌데, 해당 닉네임을 쓰고 있다면 중복
            if (!nicknameUser.getUsername().equals(currentUser.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            }
        }

        // 중복 아님 → 저장
        currentUser.setNickname(socialJoinDTO.getNickname());
        currentUser.setRole("ROLE_USER");
        userRepository.save(currentUser);

        return ResponseEntity.ok("추가 정보 저장 완료");

    }

    private String extractTokenFromCookie(HttpServletRequest request) { // 쿠키 검토 
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}