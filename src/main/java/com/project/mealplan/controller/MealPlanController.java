package com.project.mealplan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.mealplan.request.GenerateWeeklyMealPlanRequest;
import com.project.mealplan.dtos.mealplan.response.WeeklyMealPlanResponse;
import com.project.mealplan.security.jwt.SecurityUtil;
import com.project.mealplan.service.MealPlanService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meal-plans")
@Tag(name = "Meal Plan", description = "APIs for meal plan management")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping("/generate-weekly")
    public ResponseEntity<ApiResponse<WeeklyMealPlanResponse>> generateWeeklyMealPlan(
            @RequestBody(required = false) @Valid GenerateWeeklyMealPlanRequest request) {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        // If request is null, create with default values
        if (request == null) {
            request = new GenerateWeeklyMealPlanRequest();
        }

        WeeklyMealPlanResponse response = mealPlanService.generateWeeklyMealPlan(
                currentUserId,
                request.getStartDate());

        return ResponseEntity.status(201).body(ApiResponse.<WeeklyMealPlanResponse>builder()
                .status(201)
                .message("Weekly meal plan generated successfully")
                .data(response)
                .build());
    }
}
