package com.project.mealplan.common.util;

import com.project.mealplan.entity.Recipe;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.IngredientNutrition;
import com.project.mealplan.entity.RecipeIngredient;
import com.project.mealplan.repository.IngredientNutritionRepository;

import java.math.BigDecimal;
import java.util.Optional;

public class CalculateCalories {

    // This version accesses Ingredient.nutritions directly and thus requires an open persistence
    // session (works when called inside a @Transactional service method).
    public static BigDecimal computeRecipeCalories(Recipe recipe) {
        if (recipe == null || recipe.getIngredients() == null)
            return BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal total = BigDecimal.ZERO;

        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (ri == null || ri.getIngredient() == null || ri.getQuantity() == null) continue;
            Ingredient ing = ri.getIngredient();

            // find calories nutrition for ingredient from in-memory collection (requires session)
            IngredientNutrition calNut = ing.getNutritions().stream().filter(n -> n.getNutritionType() != null && n.getNutritionType().getName() != null).filter(n -> "calories".equalsIgnoreCase(n.getNutritionType().getName())).filter(n -> n.getAmountPer100g() != null).findFirst().orElse(null);

            if (calNut == null) continue;

            BigDecimal amountPer100g = calNut.getAmountPer100g();
            BigDecimal perGram = amountPer100g.multiply(new BigDecimal("0.01"));

            BigDecimal quantity = UnitConverter.toGram(BigDecimal.valueOf(ri.getQuantity()), ri.getUnit(), ri.getIngredient().getDensity());


            BigDecimal term = quantity.multiply(perGram);
            total = total.add(term);
        }

        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    // Repository-backed version: safe to call outside of a persistence session (e.g. during
    // application initialization). It queries IngredientNutrition directly to avoid lazy init.
    public static BigDecimal computeRecipeCalories(Recipe recipe, IngredientNutritionRepository ingredientNutritionRepository) {
        if (recipe == null || recipe.getIngredients() == null)
            return BigDecimal.ZERO.setScale(2, java.math.RoundingMode.HALF_UP);

        BigDecimal total = BigDecimal.ZERO;

        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (ri == null || ri.getIngredient() == null || ri.getQuantity() == null) continue;
            Ingredient ing = ri.getIngredient();
            Long ingId = ing.getId();
            if (ingId == null) continue;

            Optional<IngredientNutrition> calOpt = ingredientNutritionRepository.findFirstByIngredientIdAndNutritionTypeNameIgnoreCase(ingId, "calories");
            if (calOpt.isEmpty()) continue;

            IngredientNutrition calNut = calOpt.get();
            if (calNut.getAmountPer100g() == null) continue;

            BigDecimal amountPer100g = calNut.getAmountPer100g();
            BigDecimal perGram = amountPer100g.multiply(new BigDecimal("0.01"));

            BigDecimal quantity = UnitConverter.toGram(BigDecimal.valueOf(ri.getQuantity()), ri.getUnit(), ri.getIngredient().getDensity());

            BigDecimal term = quantity.multiply(perGram);
            total = total.add(term);
        }

        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

}