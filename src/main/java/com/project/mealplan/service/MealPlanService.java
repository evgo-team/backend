package com.project.mealplan.service;

import java.time.LocalDate;

import com.project.mealplan.dtos.mealplan.response.WeeklyMealPlanResponse;

public interface MealPlanService {
    WeeklyMealPlanResponse generateWeeklyMealPlan(Long userId, LocalDate startDate);
}
