package com.project.mealplan.controller;

import com.project.mealplan.dtos.auth.request.UserRegistrationRequest;
import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.mealplan.dtos.auth.request.LoginRequest;
import com.project.mealplan.dtos.auth.response.LoginResponse;
import com.project.mealplan.dtos.auth.response.TokenResponse;
import com.project.mealplan.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletResponse res) {
        LoginResponse data = authService.login(req, res);
        ApiResponse<LoginResponse> response = new ApiResponse<>(200, "Login successful", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest req, HttpServletResponse res) {
        authService.logout(req, res);
        ApiResponse<Void> response = new ApiResponse<>(200, "Logout successful", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(HttpServletRequest req, HttpServletResponse res) {
        TokenResponse data = authService.refreshToken(req, res);
        ApiResponse<TokenResponse> response = new ApiResponse<>(HttpStatus.OK.value(), "Refresh success", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid UserRegistrationRequest request) {
        userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<String>builder()
                        .status(201)
                        .message("User registered successfully")
                        .data(null)
                        .build());
    }

}
