package com.project.mealplan.dtos.recipe.response;

import com.project.mealplan.common.enums.IngredientUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.project.mealplan.common.enums.MealRole;
import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.dtos.mealplan.response.NutritionDetailResponse;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponseDto {
    private Long recipeId;
    private String title;
    private String description;
    private String instructions;
    private Integer cookingTimeMinutes;
    private String imageUrl;
    private BigDecimal calories;
    private NutritionDetailResponse nutrition;
    private RecipeStatus status;
    private MealRole role;
    private MealType mealType;
    private Set<String> categories; 
    private List<IngredientInfo> ingredients;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientInfo {
        // private Long id;
        private Long ingredientId;
        private String ingredientName;
        private Double quantity;
        private IngredientUnit unit;
    }
}
