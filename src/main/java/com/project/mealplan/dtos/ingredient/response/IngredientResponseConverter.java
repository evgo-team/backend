package com.project.mealplan.dtos.ingredient.response;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.project.mealplan.dtos.ingredient.NutritionInIngredient;
import com.project.mealplan.entity.Ingredient;

@Component
public class IngredientResponseConverter {

    public IngredientResponse convert(Ingredient ingredient) {
        List<NutritionInIngredient> nutritionResponses = ingredient.getNutritions().stream()
                .map(n -> new NutritionInIngredient(
                    n.getNutritionType().getId(),
                    n.getNutritionType().getName(),
                    n.getAmountPer100g(),
                    n.getNutritionType().getUnit().name()
                ))
                .toList();

        return new IngredientResponse(
            ingredient.getId(),
            ingredient.getName(),
            ingredient.getType().name(),
            nutritionResponses
        );
    }

    public List<IngredientResponse> convert(List<Ingredient> ingredients) {
        return ingredients.stream().map(this::convert).toList();
    }

    public Optional<IngredientResponse> convert(Optional<Ingredient> ingredientOpt) {
        return ingredientOpt.map(this::convert);
    }
}

