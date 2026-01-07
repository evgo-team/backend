package com.project.mealplan.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.nutrition.request.LogFoodRequest;
import com.project.mealplan.dtos.nutrition.request.LogMealConsumedRequest;
import com.project.mealplan.dtos.nutrition.request.SetNutritionGoalRequest;
import com.project.mealplan.dtos.nutrition.response.DailyFoodLogsResponse;
import com.project.mealplan.dtos.nutrition.response.DailyNutritionResponse;
import com.project.mealplan.dtos.nutrition.response.FoodLogResponse;
import com.project.mealplan.dtos.nutrition.response.MealConsumedResponse;
import com.project.mealplan.dtos.nutrition.response.NutritionGoalResponse;
import com.project.mealplan.dtos.nutrition.response.WeeklyNutritionSummaryResponse;
import com.project.mealplan.security.jwt.SecurityUtil;
import com.project.mealplan.service.NutritionTrackingService;

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
@RequestMapping("/api/nutrition")
@Tag(name = "Nutrition Tracking", description = "APIs for nutrition goal management and tracking")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class NutritionController {

        private final NutritionTrackingService nutritionTrackingService;

        @GetMapping("/goals")
        @Operation(summary = "Get nutrition goals", description = "Get user's current nutrition goal settings")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goals retrieved successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<NutritionGoalResponse>> getNutritionGoals() {
                Long currentUserId = SecurityUtil.getCurrentUserId();

                NutritionGoalResponse goals = nutritionTrackingService.getNutritionGoal(currentUserId);

                return ResponseEntity.ok(ApiResponse.<NutritionGoalResponse>builder()
                                .status(200)
                                .message(goals != null ? "Nutrition goals retrieved" : "No nutrition goals set")
                                .data(goals)
                                .build());
        }

        @PostMapping("/goals")
        @Operation(summary = "Set nutrition goals", description = "Set or update user's daily nutrition targets. Only calories is required.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goals updated successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<NutritionGoalResponse>> setNutritionGoals(
                        @Valid @RequestBody SetNutritionGoalRequest request) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("User {} setting nutrition goals: calories={}, protein={}, carbs={}, fat={}",
                                currentUserId, request.getDailyCalories(), request.getDailyProtein(),
                                request.getDailyCarbs(), request.getDailyFat());

                NutritionGoalResponse response = nutritionTrackingService.setNutritionGoal(currentUserId, request);

                return ResponseEntity.ok(ApiResponse.<NutritionGoalResponse>builder()
                                .status(200)
                                .message("Nutrition goals updated successfully")
                                .data(response)
                                .build());
        }

        @GetMapping("/goals/calculate")
        @Operation(summary = "Calculate recommended goals", description = "Calculate recommended nutrition goals based on user profile (BMR, TDEE)")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goals calculated successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "User profile incomplete"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<NutritionGoalResponse>> calculateRecommendedGoals() {
                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("Calculating recommended nutrition goals for user {}", currentUserId);

                NutritionGoalResponse response = nutritionTrackingService.calculateDefaultGoal(currentUserId);

                return ResponseEntity.ok(ApiResponse.<NutritionGoalResponse>builder()
                                .status(200)
                                .message("Recommended nutrition goals calculated based on your profile")
                                .data(response)
                                .build());
        }

        @GetMapping("/daily")
        @Operation(summary = "Get daily nutrition", description = "Get nutrition summary for a specific date. Only counts consumed meals.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Daily nutrition retrieved"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<DailyNutritionResponse>> getDailyNutrition(
                        @Parameter(description = "Date in ISO yyyy-MM-dd format. Defaults to today if not provided") @RequestParam(required = false) LocalDate date) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                if (date == null) {
                        date = LocalDate.now();
                }

                log.info("Getting daily nutrition for user {} on date {}", currentUserId, date);

                DailyNutritionResponse response = nutritionTrackingService.getDailyNutrition(currentUserId, date);

                return ResponseEntity.ok(ApiResponse.<DailyNutritionResponse>builder()
                                .status(200)
                                .message("Daily nutrition summary retrieved")
                                .data(response)
                                .build());
        }

        @GetMapping("/weekly")
        @Operation(summary = "Get weekly nutrition summary", description = "Get nutrition summary for a week. Only counts consumed meals.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Weekly summary retrieved"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<WeeklyNutritionSummaryResponse>> getWeeklyNutrition(
                        @Parameter(description = "Start date of the week in ISO yyyy-MM-dd format. Defaults to current week's Monday") @RequestParam(required = false) LocalDate startDate) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                if (startDate == null) {
                        // Default to start of current week (Monday)
                        startDate = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
                }

                log.info("Getting weekly nutrition summary for user {} starting {}", currentUserId, startDate);

                WeeklyNutritionSummaryResponse response = nutritionTrackingService.getWeeklyNutritionSummary(
                                currentUserId, startDate);

                return ResponseEntity.ok(ApiResponse.<WeeklyNutritionSummaryResponse>builder()
                                .status(200)
                                .message("Weekly nutrition summary retrieved")
                                .data(response)
                                .build());
        }

        @PatchMapping("/meals/{mealSlotId}/consumed")
        @Operation(summary = "Log meal consumed", description = "Mark a meal slot as consumed or not consumed. Only consumed meals count toward nutrition tracking.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Meal consumption logged"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Meal slot not found"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<MealConsumedResponse>> logMealConsumed(
                        @Parameter(description = "Meal slot ID", required = true) @PathVariable Long mealSlotId,
                        @RequestBody LogMealConsumedRequest request) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("User {} logging meal slot {} as consumed={}", currentUserId, mealSlotId,
                                request.getConsumed());

                MealConsumedResponse response = nutritionTrackingService.logMealConsumed(
                                currentUserId, mealSlotId, request.getConsumed());

                return ResponseEntity.ok(ApiResponse.<MealConsumedResponse>builder()
                                .status(200)
                                .message(response.getMessage())
                                .data(response)
                                .build());
        }

        // ==================== Food Log Endpoints ====================

        @PostMapping("/food-logs")
        @Operation(summary = "Log food from recipe", description = "Log food consumption from any recipe in the database (outside of meal plan)")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Food logged successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Recipe not found"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<FoodLogResponse>> logFood(
                        @Valid @RequestBody LogFoodRequest request) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("User {} logging food: recipeId={}, date={}", currentUserId, request.getRecipeId(),
                                request.getConsumeDate());

                FoodLogResponse response = nutritionTrackingService.logFood(currentUserId, request);

                return ResponseEntity.ok(ApiResponse.<FoodLogResponse>builder()
                                .status(200)
                                .message("Đã ghi nhận đồ ăn thành công")
                                .data(response)
                                .build());
        }

        @GetMapping("/food-logs")
        @Operation(summary = "Get food logs", description = "Get all food logs for a specific date")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Food logs retrieved"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<DailyFoodLogsResponse>> getFoodLogs(
                        @Parameter(description = "Date in ISO yyyy-MM-dd format. Defaults to today") @RequestParam(required = false) LocalDate date) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                if (date == null) {
                        date = LocalDate.now();
                }

                log.info("User {} getting food logs for date {}", currentUserId, date);

                DailyFoodLogsResponse response = nutritionTrackingService.getFoodLogs(currentUserId, date);

                return ResponseEntity.ok(ApiResponse.<DailyFoodLogsResponse>builder()
                                .status(200)
                                .message("Food logs retrieved")
                                .data(response)
                                .build());
        }

        @DeleteMapping("/food-logs/{foodLogId}")
        @Operation(summary = "Delete food log", description = "Delete a food log entry")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Food log deleted"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Food log not found"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<ApiResponse<Void>> deleteFoodLog(
                        @Parameter(description = "Food log ID", required = true) @PathVariable Long foodLogId) {

                Long currentUserId = SecurityUtil.getCurrentUserId();

                log.info("User {} deleting food log {}", currentUserId, foodLogId);

                nutritionTrackingService.deleteFoodLog(currentUserId, foodLogId);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(200)
                                .message("Đã xóa ghi nhận đồ ăn")
                                .build());
        }
}
