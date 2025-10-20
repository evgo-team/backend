package com.project.mealplan.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.project.mealplan.dtos.auth.request.LoginRequest;
import com.project.mealplan.dtos.auth.response.LoginResponse;
import com.project.mealplan.dtos.auth.response.TokenResponse;
import com.project.mealplan.dtos.auth.request.RefreshTokenRequest;

public interface AuthService {
    LoginResponse login(LoginRequest req, HttpServletResponse res);
    void logout(HttpServletRequest req, HttpServletResponse res, RefreshTokenRequest payload);
    TokenResponse refreshToken(RefreshTokenRequest req, HttpServletResponse res);
}
