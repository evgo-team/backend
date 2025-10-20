package com.project.mealplan.service.impl;

import com.project.mealplan.dtos.auth.request.LoginRequest;
import com.project.mealplan.dtos.auth.request.RefreshTokenRequest;
import com.project.mealplan.dtos.auth.response.LoginResponse;
import com.project.mealplan.dtos.auth.response.TokenResponse;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.security.jwt.JwtUtil;
import com.project.mealplan.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${app.jwt.expiration}")
    private long accessTtl;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshTtl;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RedisTokenService redisTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public LoginResponse login(LoginRequest req, HttpServletResponse res) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.email().trim().toLowerCase(),
                            req.password()));

            final String email = authentication.getName();
            final List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtUtil.generateAccessToken(email, roles);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            // Store refresh token in Redis
            String jti = jwtUtil.extractJti(refreshToken);
            long refreshMs = jwtUtil.getRemainingDuration(refreshToken);
            redisTokenService.storeRefreshToken(email, jti, refreshToken, Duration.ofMillis(refreshMs));

            return new LoginResponse(accessToken, refreshToken, roles);

        } catch (AuthenticationException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    @Override
    public void logout(HttpServletRequest req, HttpServletResponse res, RefreshTokenRequest payload) {
        String token = null;
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        if (token != null) {
            long ttl = jwtUtil.getRemainingDuration(token);
            if (ttl > 0) {
                redisTokenService.blacklistToken(token, Duration.ofMillis(ttl));
            }
        }

        String refreshToken = payload.refreshToken();
        if (refreshToken != null) {
            try {
                String email = jwtUtil.extractSubject(refreshToken);
                String jti = jwtUtil.extractJti(refreshToken);
                redisTokenService.deleteRefreshToken(email, jti);
            } catch (JwtException e) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest req, HttpServletResponse res) {
        String refreshToken = req.refreshToken();

        if (refreshToken == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Missing refresh token");
        }

        try {
            if (jwtUtil.isTokenExpired(refreshToken)) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "Refresh token expired");
            }
        } catch (JwtException e) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid refresh token");
        }

        String subject = jwtUtil.extractSubject(refreshToken);
        String jti = jwtUtil.extractJti(refreshToken);

        if (!redisTokenService.isValidRefreshToken(subject, jti, refreshToken)) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "Refresh token revoked or reuse detected");
        }

        redisTokenService.deleteRefreshToken(subject, jti);

        String newRefreshToken = jwtUtil.generateRefreshToken(subject);
        String newJti = jwtUtil.extractJti(newRefreshToken);

        long newRefreshMs = jwtUtil.getRemainingDuration(newRefreshToken);
        if (newRefreshMs <= 0) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to generate refresh token");
        }
        redisTokenService.storeRefreshToken(subject, newJti, newRefreshToken, Duration.ofMillis(newRefreshMs));

        UserDetails userDetails;
        try {
            userDetails = customUserDetailsService.loadUserByUsername(subject);
        } catch (UsernameNotFoundException ex) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "User not found");
        }

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String newAccessToken = jwtUtil.generateAccessToken(subject, roles);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
