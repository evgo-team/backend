package com.project.mealplan.dtos.ingredient.response;

import java.math.BigDecimal;
import java.util.List;

import com.project.mealplan.dtos.ingredient.NutritionInIngredient;

public record IngredientResponse(
    Long id,
    String name,
    String type,
    BigDecimal density,
    List<NutritionInIngredient> nutritions
) {
    
}

