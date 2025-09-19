package com.tripPlanner.project.jwt;

import com.tripPlanner.project.dto.ws.TokenUserInfo;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;



@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {


        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 검증을 진행 method getUsername, getRole, isExpired

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token){

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getType(String token){

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("type", String.class);
    }

    public String getSocialType(String token){

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("socialType", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // jwtToken 생성
    public String createJwt(String category, String type, String username, String role, String socialType, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("type", type)
                .claim("username", username)
                .claim("role", role)
                .claim("socialType", socialType)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    // 패키지/이름
    public TokenUserInfo getUserInfoOrThrow(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token is blank");
        }
        // 1) 만료 체크 (필요 시 서명/카테고리도 검증)
        if (Boolean.TRUE.equals(isExpired(token))) {
            throw new IllegalArgumentException("token is expired");
        }
        // (선택) 카테고리/타입도 검사하고 싶다면:
        // String category = getCategory(token);
        // String type = getType(token);
        // if (!"access".equals(type)) throw new IllegalArgumentException("invalid token type");

        // 2) 필요한 클레임 묶어서 반환
        String username = getUsername(token);
        String social   = getSocialType(token);
        if (username == null || social == null) {
            throw new IllegalArgumentException("username/socialType not found in token");
        }
        return new TokenUserInfo(username, social);
    }



}


