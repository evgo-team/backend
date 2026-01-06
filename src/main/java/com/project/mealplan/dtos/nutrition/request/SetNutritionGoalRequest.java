package com.project.mealplan.dtos.nutrition.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for setting or updating user's nutrition goals.
 * Only dailyCalories is required. Other macros are optional.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetNutritionGoalRequest {

    @NotNull(message = "Daily calories is required")
    @DecimalMin(value = "0", inclusive = false, message = "Daily calories must be positive")
    private BigDecimal dailyCalories;

    @DecimalMin(value = "0", inclusive = false, message = "Daily protein must be positive")
    private BigDecimal dailyProtein; // Optional

    @DecimalMin(value = "0", inclusive = false, message = "Daily carbs must be positive")
    private BigDecimal dailyCarbs; // Optional

    @DecimalMin(value = "0", inclusive = false, message = "Daily fat must be positive")
    private BigDecimal dailyFat; // Optional
}
