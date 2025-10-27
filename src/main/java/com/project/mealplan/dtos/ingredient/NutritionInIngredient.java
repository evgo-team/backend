package com.project.mealplan.dtos.ingredient;

import java.math.BigDecimal;

public record NutritionInIngredient(
	Long nutritionTypeId,
	String nutritionName, 
	BigDecimal amountPer100g,
	String unit
) {}
