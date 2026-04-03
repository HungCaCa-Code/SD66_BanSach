package org.example.datn.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts; // <-- THÊM DÒNG NÀY
import io.jsonwebtoken.SignatureAlgorithm; // <-- THÊM DÒNG NÀY
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Key bí mật (PHẢI GIỮ BÍ MẬT - nên đặt trong application.properties)
    private final String SECRET_KEY = "YourSuperSecretKeyForDATNProjectSpringBookStoreReactTaiwindCSS";
    private final Key signingKey;

    public JwtUtil() {
        byte[] keyBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Thêm vai trò vào token để FE có thể sử dụng
        claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder() // Lỗi sẽ biến mất
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
                .signWith(signingKey, SignatureAlgorithm.HS256) // Lỗi sẽ biến mất
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}