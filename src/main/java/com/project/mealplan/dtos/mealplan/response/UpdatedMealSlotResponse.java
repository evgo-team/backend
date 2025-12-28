package com.project.mealplan.dtos.mealplan.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.mealplan.common.enums.MealType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedMealSlotResponse {
    private Long mealSlotId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String date;

    private MealType mealType;

    private RecipeDetailResponse recipe;

    private NutritionSummaryResponse nutritionSummaryOfDay;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecipeDetailResponse {
        private Long recipeId;
        private String title;
        private String imageUrl;
        private BigDecimal calories;
        private NutritionSummaryResponse nutrition;
    }
}
