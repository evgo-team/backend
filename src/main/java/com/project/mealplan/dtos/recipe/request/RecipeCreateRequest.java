package com.project.mealplan.dtos.recipe.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

import com.project.mealplan.common.enums.MealRole;
import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.enums.RecipeStatus;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class RecipeCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    private String instructions;
    private Integer cookingTimeMinutes;
    private String imageUrl;
    private RecipeStatus status;
    private MealRole role;
    private MealType mealType;
    private Set<Long> categoryIds;
    private List<RecipeIngredientRequest> ingredients;
}

