package com.project.mealplan.service;

import com.project.mealplan.dtos.auth.request.UserRegistrationRequest;

public interface UserService {
    void register(UserRegistrationRequest request);
}