package com.project.mealplan.dtos.auth.response;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        List<String> roles
) {
} 