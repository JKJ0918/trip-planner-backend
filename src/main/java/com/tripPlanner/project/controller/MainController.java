package com.tripPlanner.project.controller;

import com.tripPlanner.project.dto.CustomOAuth2User;
import com.tripPlanner.project.dto.UserInfoResponseDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping("/")
    @ResponseBody
    public String mainAPI(){

        return "main route";
    }

    // 소셜 로그아웃 컨트롤러
    @GetMapping("/api/auth/me")
    public UserInfoResponseDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized - 인증되지 않음");
        }

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String socialType = user.getSocialType();

        System.out.println("로그아웃 에이밍 소셜타입 은? : "+socialType);
        return new UserInfoResponseDTO(socialType);
    }


}
