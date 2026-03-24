package com.todo.aitodo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 密钥
    private static final String SECRET = "mySecretKeymySecretKeymySecretKey";

    private final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 生成 token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)              // 存用户名
                .setIssuedAt(new Date())           // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1天
                .signWith(KEY, SignatureAlgorithm.HS256) // 签名（防篡改）
                .compact();
    }

    // 提取 username
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 校验 token（🔥关键）
    public boolean validateToken(String token) {
        try {
            getClaims(token); // 解析时会自动校验签名 + 过期
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 统一解析逻辑（内部方法）
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}