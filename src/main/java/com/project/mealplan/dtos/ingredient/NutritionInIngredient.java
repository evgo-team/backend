package com.project.mealplan.dtos.ingredient;

import java.math.BigDecimal;

public record NutritionInIngredient(
	String nutritionName, 
	BigDecimal amountPer100g,
	String unit
) {}
