package com.project.mealplan.dtos.nutrition.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for food log entry.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodLogResponse {

    private Long id;
    private Long recipeId;
    private String recipeName;
    private String recipeImageUrl;
    private LocalDateTime consumeDate;
    private BigDecimal quantity;

    // Nutrition values (calculated based on quantity)
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
}
