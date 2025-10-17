package com.project.mealplan.service.impl;

import com.project.mealplan.dtos.auth.request.LoginRequest;
import com.project.mealplan.dtos.auth.response.LoginResponse;
import com.project.mealplan.dtos.auth.response.TokenResponse;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.security.jwt.JwtUtil;
import com.project.mealplan.service.AuthService;

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
    public void logout(HttpServletRequest req, HttpServletResponse res) {

    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest req, HttpServletResponse res) {
        return new TokenResponse("newAccessToken");
    }
}
