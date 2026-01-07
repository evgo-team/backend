package com.project.mealplan.dtos.mealplan.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSlotDetailResponse {
    private Long mealSlotId;
    private Long mealPlanId;
    private Long recipeId;
    private String title;
    private String imageUrl;
    private BigDecimal calories;
    private NutritionDetailResponse nutrition;
    private Boolean consumed;
    private LocalDateTime consumedAt;
}
