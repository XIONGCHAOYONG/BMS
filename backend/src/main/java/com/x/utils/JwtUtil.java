package com.x.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 确保密钥长度安全
        validateSecretKeyLength(secretKey);

        // 生成 JWT 的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 构建签名密钥
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        // 使用非弃用 API 构建 JWT
        return Jwts.builder()
                .setClaims(claims) // 设置自定义声明（必须先设置）
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(exp)
                .compact();
    }

    public static Claims parseJWT(String secretKey, String token) {
        try {
            // 构建签名密钥
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            // 使用非弃用 API 解析 JWT
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return jws.getBody();
        } catch (JwtException | IllegalArgumentException e) {
            // 捕获 JWT 相关异常并统一处理
            throw new IllegalArgumentException("无效的 JWT token: " + e.getMessage(), e);
        }
    }

    private static void validateSecretKeyLength(String secretKey) {
        if (secretKey == null || secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("HS256 算法的密钥长度必须至少 32 字节");
        }
    }
}
