package com.project.mealplan.dtos.nutrition.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for user's nutrition goal configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionGoalResponse {
    private Long id;
    private BigDecimal dailyCalories;
    private BigDecimal dailyProtein;
    private BigDecimal dailyCarbs;
    private BigDecimal dailyFat;
}
