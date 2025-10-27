package com.project.mealplan.dtos.recipe.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import com.project.mealplan.common.enums.MealRole;
import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.enums.RecipeStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeDto {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private String instructions;

    private Integer cookingTimeMinutes;

    private String imageUrl;

    private RecipeStatus status;
    
    private MealRole role;
    
    private MealType mealType;

    private Set<String> categories; 

    @Size(max = 100, message = "Too many ingredients")
    private List<IngredientEntry> ingredients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientEntry {
        @NotNull(message = "ingredientId is required")
        private Long ingredientId;

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "Quantity must be greater than 0")
        private Double quantity;

        @NotBlank(message = "unit is required")
        private String unit;
    }
}
