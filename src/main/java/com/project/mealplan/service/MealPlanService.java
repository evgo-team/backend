package com.project.mealplan.service;

import java.time.LocalDate;

import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.dtos.mealplan.request.UpdateMealSlotRecipeRequest;
import com.project.mealplan.dtos.mealplan.response.MealSlotListResponse;
import com.project.mealplan.dtos.mealplan.response.UpdatedMealSlotResponse;
import com.project.mealplan.dtos.mealplan.response.WeeklyMealPlanResponse;

public interface MealPlanService {
    WeeklyMealPlanResponse generateWeeklyMealPlan(Long userId, LocalDate startDate);

    MealSlotListResponse getMealSlotsByDateAndMealType(Long userId, LocalDate date, MealType mealType);

    UpdatedMealSlotResponse updateMealSlotRecipe(Long userId, Long mealSlotId, UpdateMealSlotRecipeRequest request);
}
