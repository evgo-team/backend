package com.project.mealplan.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.mealplan.request.GenerateWeeklyMealPlanRequest;
import com.project.mealplan.dtos.mealplan.request.UpdateMealSlotRecipeRequest;
import com.project.mealplan.dtos.mealplan.response.MealSlotListResponse;
import com.project.mealplan.dtos.mealplan.response.UpdatedMealSlotResponse;
import com.project.mealplan.dtos.mealplan.response.WeeklyMealPlanResponse;
import com.project.mealplan.security.jwt.SecurityUtil;
import com.project.mealplan.service.MealPlanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

        @GetMapping
        @Operation(summary = "Get weekly meal plan", description = "Get the weekly meal plan for the current user containing the specified date. If date is not provided, defaults to current date.")
        public ResponseEntity<ApiResponse<WeeklyMealPlanResponse>> getWeeklyMealPlan(
                        @Parameter(description = "Date in ISO yyyy-MM-dd format to find the meal plan for") @RequestParam(required = false) LocalDate date) {

                Long currentUserId = SecurityUtil.getCurrentUserId();
                LocalDate targetDate = (date != null) ? date : LocalDate.now();

                WeeklyMealPlanResponse response = mealPlanService.getWeeklyMealPlan(
                                currentUserId,
                                targetDate);

                return ResponseEntity.ok(ApiResponse.<WeeklyMealPlanResponse>builder()
                                .status(200)
                                .message("Weekly meal plan retrieved successfully")
                                .data(response)
                                .build());
        }

        @GetMapping("/slot")
        public ResponseEntity<ApiResponse<MealSlotListResponse>> getMealSlotsByDateAndMealType(
                        @Parameter(description = "Date in ISO yyyy-MM-dd format", required = true) @RequestParam(required = true) LocalDate date,
                        @Parameter(description = "Meal type enum", required = true) @RequestParam(required = true) MealType mealType) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("Fetching meal slots for user: {}, date: {}, mealType: {}", currentUserId, date, mealType);

                MealSlotListResponse response = mealPlanService.getMealSlotsByDateAndMealType(
                                currentUserId,
                                date,
                                mealType);

                return ResponseEntity.ok(ApiResponse.<MealSlotListResponse>builder()
                                .status(200)
                                .message("Meal slots retrieved successfully")
                                .data(response)
                                .build());
        }

        @PatchMapping("/slots/{mealSlotId}")
        @Operation(summary = "Update recipe in a meal slot", description = "Replace the recipe in a meal slot with a new recipe. Returns updated slot info with daily nutrition summary.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Meal slot updated successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Recipe not found or invalid request"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - meal slot does not belong to user"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Meal slot not found")
        })
        public ResponseEntity<ApiResponse<UpdatedMealSlotResponse>> updateMealSlotRecipe(
                        @Parameter(description = "Meal slot ID", required = true) @PathVariable Long mealSlotId,
                        @Valid @RequestBody UpdateMealSlotRecipeRequest request) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("User {} updating meal slot {} with recipe {}", currentUserId, mealSlotId,
                                request.getRecipeId());

                UpdatedMealSlotResponse response = mealPlanService.updateMealSlotRecipe(
                                currentUserId,
                                mealSlotId,
                                request);

                return ResponseEntity.ok(ApiResponse.<UpdatedMealSlotResponse>builder()
                                .status(200)
                                .message("Meal slot updated successfully")
                                .data(response)
                                .build());
        }
}
