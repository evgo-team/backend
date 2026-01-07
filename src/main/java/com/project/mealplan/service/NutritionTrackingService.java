package com.project.mealplan.service;

import java.time.LocalDate;

import com.project.mealplan.dtos.nutrition.request.LogFoodRequest;
import com.project.mealplan.dtos.nutrition.request.SetNutritionGoalRequest;
import com.project.mealplan.dtos.nutrition.response.DailyFoodLogsResponse;
import com.project.mealplan.dtos.nutrition.response.DailyNutritionResponse;
import com.project.mealplan.dtos.nutrition.response.FoodLogResponse;
import com.project.mealplan.dtos.nutrition.response.MealConsumedResponse;
import com.project.mealplan.dtos.nutrition.response.NutritionGoalResponse;
import com.project.mealplan.dtos.nutrition.response.WeeklyNutritionSummaryResponse;

/**
 * Service interface for nutrition tracking operations.
 */
public interface NutritionTrackingService {

    /**
     * Set or update user's nutrition goals.
     * 
     * @param userId  User ID
     * @param request Nutrition goal data
     * @return Updated nutrition goal
     */
    NutritionGoalResponse setNutritionGoal(Long userId, SetNutritionGoalRequest request);

    /**
     * Get user's current nutrition goals.
     * 
     * @param userId User ID
     * @return Current nutrition goals or null if not set
     */
    NutritionGoalResponse getNutritionGoal(Long userId);

    /**
     * Calculate recommended nutrition goals based on user's profile.
     * Uses BMR (Mifflin-St Jeor) and activity level for TDEE.
     * 
     * @param userId User ID
     * @return Calculated nutrition goals
     */
    NutritionGoalResponse calculateDefaultGoal(Long userId);

    /**
     * Get daily nutrition summary for a specific date.
     * Includes consumed meals from plan + food logs.
     * 
     * @param userId User ID
     * @param date   Target date
     * @return Daily nutrition data with progress
     */
    DailyNutritionResponse getDailyNutrition(Long userId, LocalDate date);

    /**
     * Get weekly nutrition summary starting from a specific date.
     * Includes 7 days of data with averages.
     * 
     * @param userId    User ID
     * @param startDate Start date of the week
     * @return Weekly nutrition summary
     */
    WeeklyNutritionSummaryResponse getWeeklyNutritionSummary(Long userId, LocalDate startDate);

    /**
     * Log a meal slot as consumed or not consumed.
     * Only consumed meals count toward nutrition tracking.
     * 
     * @param userId     User ID
     * @param mealSlotId Meal slot ID
     * @param consumed   Whether the meal was consumed
     * @return Confirmation response
     */
    MealConsumedResponse logMealConsumed(Long userId, Long mealSlotId, Boolean consumed);

    // ==================== Food Log Methods ====================

    /**
     * Log food consumption from a recipe (outside of meal plan).
     * 
     * @param userId  User ID
     * @param request Food log data
     * @return Created food log entry
     */
    FoodLogResponse logFood(Long userId, LogFoodRequest request);

    /**
     * Get all food logs for a specific date.
     * 
     * @param userId User ID
     * @param date   Target date
     * @return List of food logs for the date
     */
    DailyFoodLogsResponse getFoodLogs(Long userId, LocalDate date);

    /**
     * Delete a food log entry.
     * 
     * @param userId    User ID
     * @param foodLogId Food log ID
     */
    void deleteFoodLog(Long userId, Long foodLogId);
}
