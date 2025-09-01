package com.tripPlanner.project.service.jwt;

import com.tripPlanner.project.jwt.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JWTUtil jwtUtil;


    // jwt Token Parsing
    public Claims parseClaims(String jwt) {
        return Jwts.parser()               // 0.12.x
                .verifyWith(jwtUtil.getSecretKey())  // setSigningKey 아님!
                .build()
                .parseSignedClaims(jwt) // parseClaimsJws 아님!
                .getPayload();          // Claims
    }

    // exp 가져오기(토큰 만료 시간)
    public long getExpEpochSec(String jwt) {
        Date exp = parseClaims(jwt).getExpiration();
        return exp.toInstant().getEpochSecond();
    }

    // 현재 epoch(현재시각)
    public long nowEpochSec() {
        return Instant.now().getEpochSecond();
    }


}
