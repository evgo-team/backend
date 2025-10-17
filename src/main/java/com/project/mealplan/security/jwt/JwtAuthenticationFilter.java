package com.project.mealplan.security.jwt;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.service.impl.CustomUserDetailsService;
import com.project.mealplan.service.impl.RedisTokenService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {


        String path = request.getRequestURI();

        // Skip JWT processing for auth endpoints
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = null;
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // check blacklist
                if (redisTokenService.isTokenBlacklisted(token)) {
                    throw new AppException(ErrorCode.UNAUTHORIZED, "Token is blacklisted");
                }

                String username = jwtUtil.extractSubject(token);
                if (!jwtUtil.isTokenExpired(token)) {
                    var customUserDetails = customUserDetailsService.loadUserByUsername(username);
                    var auth = new UsernamePasswordAuthenticationToken(
                            customUserDetails, null, customUserDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (ExpiredJwtException ex) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "Token has expired");
            } catch (JwtException ex) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "Invalid token");
            } catch (RuntimeException ex) {
                throw new AppException(ErrorCode.UNAUTHORIZED, ex.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}