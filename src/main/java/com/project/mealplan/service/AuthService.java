package com.project.mealplan.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.project.mealplan.dtos.auth.request.LoginRequest;
import com.project.mealplan.dtos.auth.response.LoginResponse;
import com.project.mealplan.dtos.auth.response.TokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest req, HttpServletResponse res);
    void logout(HttpServletRequest req, HttpServletResponse res);
    TokenResponse refreshToken(HttpServletRequest req, HttpServletResponse res);
}
