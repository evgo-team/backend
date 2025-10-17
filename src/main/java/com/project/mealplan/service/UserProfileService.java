package com.project.mealplan.service;

import com.project.mealplan.dtos.profile.request.UpdateProfileRequest;
import com.project.mealplan.dtos.profile.respond.UserProfileResponse;

public interface UserProfileService {
    void updateProfile(Long userId, UpdateProfileRequest request);
    UserProfileResponse getProfile(Long userId);
}