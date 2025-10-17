package com.project.mealplan.service.impl;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:access:";
    private static final String REFRESH_PREFIX = "refresh:";

    // add access token into blacklist
    public void blacklistToken(String token, Duration duration) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, true, duration);
    }

    // check if access token in blacklist
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().get(BLACKLIST_PREFIX + token));
    }

    // store refresh token with email + jti as key
    public void storeRefreshToken(String email, String jti, String token, Duration duration) {
        String key = REFRESH_PREFIX + email + ":" + jti;
        redisTemplate.opsForValue().set(key, token, duration);
    }

    // check if refresh token valid
    public boolean isValidRefreshToken(String email, String jti, String token) {
        String key = REFRESH_PREFIX + email + ":" + jti;
        Object stored = redisTemplate.opsForValue().get(key);
        return token.equals(stored);
    }

    // delete specific refresh token
    public void deleteRefreshToken(String email, String jti) {
        String key = REFRESH_PREFIX + email + ":" + jti;
        redisTemplate.delete(key);
    }

    // delete all refresh tokens of a user
    public void deleteAllRefreshTokens(String email) {
        String pattern = REFRESH_PREFIX + email + ":*";
        redisTemplate.keys(pattern).forEach(redisTemplate::delete);
    }
}