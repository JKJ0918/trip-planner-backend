package com.tripPlanner.project.config;

import com.tripPlanner.project.jwt.CustomLogoutFilter;
import com.tripPlanner.project.jwt.JWTFilter;
import com.tripPlanner.project.jwt.JWTUtil;
import com.tripPlanner.project.jwt.LoginFilter;
import com.tripPlanner.project.oauth2.CustomSuccessHandler;
import com.tripPlanner.project.repository.RefreshRepository;
import com.tripPlanner.project.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;

    private final RefreshRepository refreshRepository;

    private final AuthenticationConfiguration authenticationConfiguration;


    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,CustomSuccessHandler customSuccessHandler,
                          JWTUtil jwtUtil, AuthenticationConfiguration authenticationConfiguration, RefreshRepository refreshRepository){
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.authenticationConfiguration = authenticationConfiguration;
        this.refreshRepository = refreshRepository;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // cors 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);


        // 일반 로그인 loginFilter 추가
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
                // addFilterAt(추가한다) 새로운 loginfiter를 기존 필터 의 클래스인 UsernamePasswordAuthenticationFilter 위치에.

        // 로그아웃 필터
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );
        // userInfoEndpoint : OAuth2 로그인이 완료된 후, 사용자의 정보를 처리할 때 사용하는 엔드포인트

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").permitAll() //  메인 페이지
                        .requestMatchers("/api/auth/me","/api/auth/logout").permitAll() // 로그인 로그아웃
                        .requestMatchers("/join","/login","/main").permitAll() // 회원가입
                        .requestMatchers("/reissue").permitAll() // refresh Token
                        .requestMatchers("/api/user/additional-info","/socialJoin").permitAll()//hasRole("USER_ROLE_A") // refresh Token
                        .requestMatchers( "/api/locations", "/api/journals/auth/me", "/api/journals").permitAll() // 초반 위치 목록, 
                        .requestMatchers("/api/journals/public","/api/journals/public/{id}" ).permitAll() // 게시글 목록, 게시물 상세보기
                        .requestMatchers("/api/journals/public/edit/{id}" ).permitAll() // 게시글 수정
                        .requestMatchers("/api/images/upload","/api/images/edit/upload", "/api/images/edit/delete" ).permitAll() // 게시글 이미지 관련

                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();

    }

}
