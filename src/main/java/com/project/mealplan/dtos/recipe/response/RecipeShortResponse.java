package com.project.mealplan.dtos.recipe.response;

import java.util.Set;

import com.project.mealplan.common.enums.RecipeStatus;
import java.math.BigDecimal;
public record RecipeShortResponse(
    Long recipeId,
    String title,
    String imageUrl,
    RecipeStatus status,
    Set<String> categories,
    Integer cookingTimeMinutes,
    BigDecimal calories
) {}
