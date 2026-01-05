package com.project.mealplan.dtos.nutrition.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for daily nutrition summary with progress tracking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyNutritionResponse {

    private LocalDate date;

    // Actual consumed values
    private BigDecimal consumedCalories;
    private BigDecimal consumedProtein;
    private BigDecimal consumedCarbs;
    private BigDecimal consumedFat;

    // Goal values
    private BigDecimal goalCalories;
    private BigDecimal goalProtein;
    private BigDecimal goalCarbs;
    private BigDecimal goalFat;

    // Progress percentage (0-100+)
    private BigDecimal caloriesProgress;
    private BigDecimal proteinProgress;
    private BigDecimal carbsProgress;
    private BigDecimal fatProgress;
}
