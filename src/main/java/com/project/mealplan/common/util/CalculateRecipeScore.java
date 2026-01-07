package com.project.mealplan.common.util;

import com.project.mealplan.entity.Recipe;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * Utility class for calculating recipe scores based on various factors.
 * Used for recipe suggestions and meal plan generation.
 */
public class CalculateRecipeScore {

    // Scoring weights
    private static final double CALORIE_WEIGHT = 0.5;  // 50% weight for calorie fit
    private static final double PANTRY_WEIGHT = 0.3;   // 30% weight for pantry match
    private static final double FAVORITE_WEIGHT = 0.2; // 20% weight for favorite status

    private CalculateRecipeScore() {
        // Private constructor to prevent instantiation
    }

    /**
     * Calculate recipe score based on calorie fit, pantry match, and favorite status
     * 
     * @param recipe The recipe to score
     * @param targetCalories Target calorie amount for the meal
     * @param pantryIngredientIds Set of ingredient IDs available in user's pantry
     * @param favoriteRecipeIds Set of recipe IDs marked as favorites by the user
     * @return Score between 0.0 and 1.0
     */
    public static double calculateRecipeScore(
            Recipe recipe,
            BigDecimal targetCalories,
            Set<Long> pantryIngredientIds,
            Set<Long> favoriteRecipeIds) {

        // (A) Calorie Fit Score (50%)
        double calorieScore = calculateCalorieScore(recipe.getCalories(), targetCalories);

        // (B) Pantry Match Score (30%)
        double pantryScore = calculatePantryScore(recipe, pantryIngredientIds);

        // (C) Favorite Score (20%)
        double favoriteScore = favoriteRecipeIds.contains(recipe.getRecipeId()) ? 1.0 : 0.0;

        // Total weighted score
        double totalScore = CALORIE_WEIGHT * calorieScore
                + PANTRY_WEIGHT * pantryScore
                + FAVORITE_WEIGHT * favoriteScore;

        return Math.max(0.0, Math.min(1.0, totalScore)); // Clamp to [0, 1]
    }

    /**
     * Calculate calorie fit score (closer to target = higher score)
     * Uses exponential decay to penalize recipes far from target
     * 
     * @param recipeCalories Calories in the recipe
     * @param targetCalories Target calorie amount
     * @return Score between 0.0 and 1.0
     */
    public static double calculateCalorieScore(BigDecimal recipeCalories, BigDecimal targetCalories) {
        if (recipeCalories == null || targetCalories == null || targetCalories.compareTo(BigDecimal.ZERO) == 0) {
            return 0.5; // Neutral score if data missing
        }

        BigDecimal diff = recipeCalories.subtract(targetCalories).abs();
        BigDecimal ratio = diff.divide(targetCalories, 4, RoundingMode.HALF_UP);

        // Linear decay approach
        double score = 1.0 - ratio.doubleValue();
        return Math.max(0.0, Math.min(1.0, score));
    }

    /**
     * Calculate pantry match score (more ingredients in pantry = higher score)
     * 
     * @param recipe The recipe to evaluate
     * @param pantryIngredientIds Set of ingredient IDs available in pantry
     * @return Score between 0.0 and 1.0 (ratio of matched ingredients)
     */
    public static double calculatePantryScore(Recipe recipe, Set<Long> pantryIngredientIds) {
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return 0.0;
        }

        long matchCount = recipe.getIngredients().stream()
                .filter(ri -> ri.getIngredient() != null && pantryIngredientIds.contains(ri.getIngredient().getId()))
                .count();

        return (double) matchCount / recipe.getIngredients().size();
    }
}
