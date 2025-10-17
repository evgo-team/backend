package com.project.mealplan.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class JwtUtil {
    private final Key key;
    private final long accessTtl;
    private final long refreshTtl;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long accessTtl,
            @Value("${app.jwt.refresh-expiration}") long refreshTtl
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String generateAccessToken(String subject, Collection<String> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authorities);
        return buildToken(subject, claims, accessTtl);
    }

    public String generateRefreshToken(String subject) {
        return buildToken(subject, Collections.emptyMap(), refreshTtl);
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return parseClaims(token).get("roles", List.class);
    }

    public String extractJti(String token) {
        return parseClaims(token).getId();
    }

    public boolean isTokenExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public long getRemainingDuration(String token) {
        try {
            Date exp = parseClaims(token).getExpiration();
            long diff = exp.getTime() - System.currentTimeMillis();
            return Math.max(diff, 0);
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }


    private String buildToken(String sub, Map<String, Object> claims, long ttl) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttl);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setClaims(new HashMap<>(claims)).setSubject(sub).setId(jti)
                .setIssuedAt(now).setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
//    private Key getSigningKey() {
//        return this.key;
//    }

//    public String extractEmail(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }

//    public Long extractUserId(String token) {
//        return extractClaim(token, claims -> claims.get("userId", Long.class));
//    }
//
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }

//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }

//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }


//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractEmail(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
}
//@Value("${jwt.secret}")
//private String secretKey;
//
//@Value("${jwt.expiration}")
//private Long expiration;

