package com.tripPlanner.project.oauth2;

import com.tripPlanner.project.dto.CustomOAuth2User;
import com.tripPlanner.project.entity.RefreshEntity;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    private RefreshRepository refreshRepository;

    public CustomSuccessHandler(JWTUtil jwtUtil, RefreshRepository refreshRepository){

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("CustomSuccessHandler authentication 출력 확인 : " + authentication);

        //유저 정보
        String username = authentication.getName();

        // 소셜 타입
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String socialType = user.getSocialType();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();


        //토큰 생성
        String accessToken = jwtUtil.createJwt("access", "social", username, role, socialType, 1800000L);
        String refresh = jwtUtil.createJwt("refresh", "social", username, role, socialType, 86400000L);
        // 토큰 만들때 username은 db의 name에 해당하는 값.
        //Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L);

        response.addCookie(createCookie("Authorization", accessToken));
        // 띄어쓰기 포함 일 경우 쿠키 오류 발생가능. JWT도 보안상 Bearer 접두사를 생략하는 경우가 많음.
        response.addCookie(createCookie("refresh", refresh));

        if(role.equals("ROLE_USER_A")){
            // 추가정보 미기입 회원
            response.sendRedirect("http://localhost:3000/socialJoin");
        }else{
            // 기존회원
            response.sendRedirect("http://localhost:3000/main"); // 로그인 성공시 이동 페이지
        }



    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60); // 60*60*60 = 216,000초 -> 60시간
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // 자바스크립트가 쿠키를 가져가지 못함

        return cookie;
    }

}
