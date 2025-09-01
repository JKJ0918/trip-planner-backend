package com.tripPlanner.project.jwt;

import com.tripPlanner.project.dto.CustomOAuth2User;
import com.tripPlanner.project.dto.CustomUserDetails;
import com.tripPlanner.project.dto.UserDTO;
import com.tripPlanner.project.entity.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

// 스프링 시큐리티 filter chain에 요청에 담긴 JWT를 검증하기 위한 커스텀 필터 등록
// 해당 필터를 통해 요청 쿠키에 JWT가 존재하는 경우 JWT를 검증하고 강제로SecurityContextHolder에 세션을 생성
// (이 세션은 STATLESS 상태로 관리되기 때문에 해당 요청이 끝나면 소멸, OncePerRequestFilter)
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil){
        this.jwtUtil = jwtUtil;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractAccessToken(request);
        System.out.println("doFilterInternal access Token 검증 : " + accessToken);
        // accessToken 이 없을 경우 다음 필터로 넘김
        if (accessToken == null) {
            System.out.println("accessToken null");
            filterChain.doFilter(request, response);
            return;
        }
        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return; // 토큰 만료시 다음 필터가아닌 만료됐다는 응답 return 만료 돼었다는 응답코드 발생
        }

        // 토큰이 access인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        System.out.println("doFilterInternal Token category 검증 : " + category);
        if (!category.equals("access")) { // access가 아닐 경우

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;

            // 필요한 응용 상태코드를 발급하여 프론트에서 리프레시 토큰을 발급하도록 하던가 해야함.
        }
        // 여기까지 토큰 검증완료


        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);
        String type = jwtUtil.getType(accessToken);
        String socialType = jwtUtil.getSocialType(accessToken);

        Authentication authToken = null;

        if (type.equals("local")) {

            // JWT 로그인 사용자
            //userEntity를 생성하여 값 set
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setRole(role);
            userEntity.setSocialType(socialType);

            //UserDetails에 회원 정보 객체 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

            //스프링 시큐리티 인증 토큰 생성
            authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());


        } else {
            // OAuth2 로그인 사용자
            //userDTO를 생성하여 값 set
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setRole(role);
            userDTO.setSocialType(socialType);

            //UserDetails에 회원 정보 객체 담기
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

            //스프링 시큐리티 인증 토큰 생성
            authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        }


        // 세션에 사용자 등록
        if (authToken != null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);


    }
}
