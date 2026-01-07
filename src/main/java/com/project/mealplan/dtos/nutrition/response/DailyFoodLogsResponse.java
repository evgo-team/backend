package com.project.mealplan.dtos.nutrition.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for listing food logs for a specific date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyFoodLogsResponse {

    private LocalDate date;
    private List<FoodLogResponse> foodLogs;
    private int totalEntries;

    // Total nutrition for the day (from food logs only)
    private BigDecimal totalCalories;
    private BigDecimal totalProtein;
    private BigDecimal totalCarbs;
    private BigDecimal totalFat;
}
