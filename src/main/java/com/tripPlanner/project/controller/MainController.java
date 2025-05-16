package com.tripPlanner.project.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    // 로그인 상태확인
    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getLoginStatus(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(false); // 인증 안 됨
        }
        return ResponseEntity.ok(true); // 인증됨
    }



    // 로그아웃 컨트롤러
    @PostMapping("/api/auth/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie authCookie = new Cookie("Authorization", null);
        authCookie.setMaxAge(0);
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);
        response.addCookie(authCookie);

        Cookie jsessionCookie = new Cookie("JSESSIONID", null);
        jsessionCookie.setMaxAge(0);
        jsessionCookie.setPath("/");
        jsessionCookie.setHttpOnly(true);
        response.addCookie(jsessionCookie);

        return ResponseEntity.ok().build();
    }




}
