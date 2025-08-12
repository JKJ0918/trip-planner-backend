package com.tripPlanner.project.controller;

import com.tripPlanner.project.jwt.JWTFilter;
import com.tripPlanner.project.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class MainController {

    private JWTUtil jwtUtil;
    public MainController(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/")
    @ResponseBody
    public String mainAPI(){

        return "main route";
    }

    // 소셜 로그아웃 컨트롤러?? 주석 잘못쓴듯 기능 자체는 그냥 소셜 타입을 주는 느낌임
    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request){

        String token = extractAccessToken(request);
        String socialType = jwtUtil.getSocialType(token);

        return ResponseEntity.ok(Map.of(
           "socialType", socialType
        ));


    }

    private String extractAccessToken(HttpServletRequest request){
        // 1. OAuth2 방식: 쿠키에서 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 2. JWT 방식: 헤더에서 찾기
        String header = request.getHeader("access");

        return null;
    }

}
