package com.project.mealplan.controller;

import com.project.mealplan.dtos.profile.request.UpdateProfileRequest;
import com.project.mealplan.dtos.profile.respond.UserProfileResponse;
import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.security.jwt.SecurityUtil;
import com.project.mealplan.service.UserProfileService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "APIs for user operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserProfileService userProfileService;

    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        UserProfileResponse profile = userProfileService.getProfile(currentUserId);

        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .status(200)
                .message("User profile retrieved successfully")
                .data(profile)
                .build());
    }

    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<String>> updateUserProfile(
            @RequestBody @Valid UpdateProfileRequest request) {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        userProfileService.updateProfile(currentUserId, request);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(200)
                .message("Cập nhật thành công")
                .data(null)
                .build());
    }

}
