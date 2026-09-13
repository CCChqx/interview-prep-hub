package com.studyhub.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private long accessExpiration; //ms

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    // 生成 token：payload 里放 useId +username，设定过期时间
    public String generateToken(Long userId,String username,String type) {
        SecretKey key = getKey();

        long expire = "access".equals(type) ? accessExpiration : refreshExpiration;
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", type).claim("username",username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(key)
                .compact();
    }

    // 解析 token ：验签 + 验过期，失败会抛异常
    public Claims parseToken(String token) {
        SecretKey key = getKey();
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
