package com.tripPlanner.project.controller;

import com.tripPlanner.project.jwt.JWTFilter;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@Controller
public class MainController {

    private JWTUtil jwtUtil;
    private JwtService jwtService;
    public MainController(JWTUtil jwtUtil, JwtService jwtService){
        this.jwtUtil = jwtUtil;
        this.jwtService = jwtService;
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

    // 토큰 만료 시간 표시
    @GetMapping("/api/auth/session")
    public ResponseEntity<?> getSession(HttpServletRequest request){
        String token = extractAccessToken(request);
        System.out.println("토큰이 가져와졌나?"+token);
        // 토큰 X
        if(token == null || token.isBlank()){
            return ResponseEntity.status(401).body(Map.of(
                    "message", "로그인이 필요합니다."
            ));
        }
        // 토큰 O
        try {
            long now = jwtService.nowEpochSec();
            System.out.println("현재 now 값은??"+now);
            long exp = jwtService.getExpEpochSec(token);
            System.out.println("현재 exp 만료 값은??"+exp);
            long remaining = Math.max(0, exp-now);

            return ResponseEntity.ok(Map.of(
                "nowEpoch", now,
                "expEpoch", exp,
                "remainingSeconds", remaining
            ));
        } catch (ExpiredJwtException e) {
            long exp = e.getClaims().getExpiration().toInstant().getEpochSecond();
            return ResponseEntity.status(401).body(Map.of(
                    "message", "세션이 만료되었습니다.",
                    "expired", true,
                    "expEpoch", exp
            ));
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "유효하지 않은 토큰입니다."
            ));
        }

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
