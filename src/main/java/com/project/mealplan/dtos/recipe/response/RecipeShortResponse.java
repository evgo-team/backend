package com.project.mealplan.dtos.recipe.response;

import java.util.Set;

import com.project.mealplan.common.enums.RecipeStatus;

public record RecipeShortResponse(
    Long recipeId,
    String title,
    RecipeStatus status,
    Set<String> categories
) {}
