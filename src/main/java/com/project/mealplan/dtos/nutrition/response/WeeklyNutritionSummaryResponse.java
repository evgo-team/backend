package com.project.mealplan.dtos.nutrition.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for weekly nutrition summary with daily breakdown and averages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyNutritionSummaryResponse {

    private LocalDate startDate;
    private LocalDate endDate;

    // Daily breakdown
    private List<DailyNutritionResponse> dailyData;

    // Weekly averages
    private BigDecimal avgCalories;
    private BigDecimal avgProtein;
    private BigDecimal avgCarbs;
    private BigDecimal avgFat;

    // Goal values for reference
    private BigDecimal goalCalories;
    private BigDecimal goalProtein;
    private BigDecimal goalCarbs;
    private BigDecimal goalFat;

    // Average progress percentage
    private BigDecimal avgCaloriesProgress;
    private BigDecimal avgProteinProgress;
    private BigDecimal avgCarbsProgress;
    private BigDecimal avgFatProgress;
}
