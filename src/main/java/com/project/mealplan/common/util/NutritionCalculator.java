package com.project.mealplan.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.project.mealplan.common.enums.ActivityLevel;
import com.project.mealplan.common.enums.Gender;

/**
 * Utility class for nutrition calculations including BMR, TDEE, and macro
 * distribution.
 */
public class NutritionCalculator {

    // Macro distribution percentages
    private static final BigDecimal PROTEIN_PERCENTAGE = new BigDecimal("0.25"); // 25%
    private static final BigDecimal CARBS_PERCENTAGE = new BigDecimal("0.45"); // 45%
    private static final BigDecimal FAT_PERCENTAGE = new BigDecimal("0.30"); // 30%

    // Calories per gram of macronutrient
    private static final BigDecimal CALORIES_PER_GRAM_PROTEIN = new BigDecimal("4");
    private static final BigDecimal CALORIES_PER_GRAM_CARBS = new BigDecimal("4");
    private static final BigDecimal CALORIES_PER_GRAM_FAT = new BigDecimal("9");

    private NutritionCalculator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Calculate Basal Metabolic Rate using Mifflin-St Jeor equation.
     * 
     * For men: BMR = 10 × weight(kg) + 6.25 × height(cm) − 5 × age + 5
     * For women: BMR = 10 × weight(kg) + 6.25 × height(cm) − 5 × age − 161
     *
     * @param weight Weight in kg
     * @param height Height in cm
     * @param age    Age in years
     * @param gender User's gender
     * @return BMR in calories
     */
    public static BigDecimal calculateBMR(BigDecimal weight, BigDecimal height, Integer age, Gender gender) {
        if (weight == null || height == null || age == null || gender == null) {
            return null;
        }

        BigDecimal bmr = new BigDecimal("10").multiply(weight)
                .add(new BigDecimal("6.25").multiply(height))
                .subtract(new BigDecimal("5").multiply(new BigDecimal(age)));

        if (gender == Gender.MALE) {
            bmr = bmr.add(new BigDecimal("5"));
        } else {
            bmr = bmr.subtract(new BigDecimal("161"));
        }

        return bmr.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE) based on BMR and activity
     * level.
     *
     * @param bmr           Basal Metabolic Rate
     * @param activityLevel User's activity level
     * @return TDEE in calories
     */
    public static BigDecimal calculateTDEE(BigDecimal bmr, ActivityLevel activityLevel) {
        if (bmr == null || activityLevel == null) {
            return null;
        }

        BigDecimal multiplier = getActivityMultiplier(activityLevel);
        return bmr.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get the activity multiplier for TDEE calculation.
     */
    public static BigDecimal getActivityMultiplier(ActivityLevel activityLevel) {
        return switch (activityLevel) {
            case SEDENTARY -> new BigDecimal("1.2"); // Little or no exercise
            case LIGHT -> new BigDecimal("1.375"); // Light exercise 1-3 days/week
            case MODERATE -> new BigDecimal("1.55"); // Moderate exercise 3-5 days/week
            case ACTIVE -> new BigDecimal("1.725"); // Hard exercise 6-7 days/week
            case VERY_ACTIVE -> new BigDecimal("1.9"); // Very hard exercise, physical job
        };
    }

    /**
     * Calculate recommended daily protein intake in grams based on TDEE.
     * Uses 25% of total calories from protein (4 cal/gram).
     */
    public static BigDecimal calculateDailyProtein(BigDecimal tdee) {
        if (tdee == null) {
            return null;
        }
        return tdee.multiply(PROTEIN_PERCENTAGE)
                .divide(CALORIES_PER_GRAM_PROTEIN, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate recommended daily carbohydrate intake in grams based on TDEE.
     * Uses 45% of total calories from carbs (4 cal/gram).
     */
    public static BigDecimal calculateDailyCarbs(BigDecimal tdee) {
        if (tdee == null) {
            return null;
        }
        return tdee.multiply(CARBS_PERCENTAGE)
                .divide(CALORIES_PER_GRAM_CARBS, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate recommended daily fat intake in grams based on TDEE.
     * Uses 30% of total calories from fat (9 cal/gram).
     */
    public static BigDecimal calculateDailyFat(BigDecimal tdee) {
        if (tdee == null) {
            return null;
        }
        return tdee.multiply(FAT_PERCENTAGE)
                .divide(CALORIES_PER_GRAM_FAT, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate progress percentage.
     * 
     * @param consumed Amount consumed
     * @param goal     Target goal
     * @return Percentage (0-100+)
     */
    public static BigDecimal calculateProgress(BigDecimal consumed, BigDecimal goal) {
        if (consumed == null || goal == null || goal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return consumed.multiply(new BigDecimal("100"))
                .divide(goal, 2, RoundingMode.HALF_UP);
    }
}
