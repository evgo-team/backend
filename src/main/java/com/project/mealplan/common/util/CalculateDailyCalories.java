package com.project.mealplan.common.util;

import com.project.mealplan.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for calculating daily calorie requirements based on user profile.
 * Uses the Mifflin-St Jeor equation for BMR (Basal Metabolic Rate) calculation.
 */
public class CalculateDailyCalories {

    private static final BigDecimal DEFAULT_CALORIE_TARGET = BigDecimal.valueOf(2000);

    private CalculateDailyCalories() {
        // Private constructor to prevent instantiation
    }

    /**
     * Calculate daily calorie target using Mifflin-St Jeor equation
     * Formula:
     * - Men: BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5
     * - Women: BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161
     * TDEE = BMR * Activity Multiplier
     * 
     * @param user User with profile data (weight, height, age, gender, activity level)
     * @return Daily calorie target (TDEE - Total Daily Energy Expenditure)
     */
    public static BigDecimal calculateDailyCalorieTarget(User user) {
        // Check if profile is complete
        if (user == null || user.getWeight() == null || user.getHeight() == null || user.getAge() == null) {
            return DEFAULT_CALORIE_TARGET;
        }

        BigDecimal weight = user.getWeight(); // kg
        BigDecimal height = user.getHeight(); // cm
        int age = user.getAge();

        // BMR calculation using Mifflin-St Jeor equation
        BigDecimal bmr;
        if (user.getGender() != null && user.getGender().toString().equalsIgnoreCase("MALE")) {
            // Male: BMR = 10*weight(kg) + 6.25*height(cm) - 5*age + 5
            bmr = BigDecimal.valueOf(10).multiply(weight)
                    .add(BigDecimal.valueOf(6.25).multiply(height))
                    .subtract(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(age)))
                    .add(BigDecimal.valueOf(5));
        } else {
            // Female: BMR = 10*weight(kg) + 6.25*height(cm) - 5*age - 161
            bmr = BigDecimal.valueOf(10).multiply(weight)
                    .add(BigDecimal.valueOf(6.25).multiply(height))
                    .subtract(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(age)))
                    .subtract(BigDecimal.valueOf(161));
        }

        // Activity multiplier (default to moderate = 1.55)
        String activityLevelName = user.getActivityLevel() != null 
                ? user.getActivityLevel().name() 
                : "MODERATE";
        BigDecimal activityMultiplier = getActivityMultiplier(activityLevelName);
        
        // TDEE = BMR * Activity Factor
        BigDecimal tdee = bmr.multiply(activityMultiplier);

        return tdee.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Get activity multiplier based on activity level
     * 
     * Activity Levels:
     * - SEDENTARY: Little to no exercise (1.2)
     * - LIGHT: Light exercise 1-3 days/week (1.375)
     * - MODERATE: Moderate exercise 3-5 days/week (1.55)
     * - ACTIVE: Heavy exercise 6-7 days/week (1.725)
     * - VERY_ACTIVE: Very heavy exercise, physical job (1.9)
     * 
     * @param activityLevel Activity level as string
     * @return Activity multiplier
     */
    public static BigDecimal getActivityMultiplier(String activityLevel) {
        if (activityLevel == null) {
            return BigDecimal.valueOf(1.55); // Moderate default
        }

        return switch (activityLevel.toUpperCase()) {
            case "SEDENTARY" -> BigDecimal.valueOf(1.2);
            case "LIGHT" -> BigDecimal.valueOf(1.375);
            case "MODERATE" -> BigDecimal.valueOf(1.55);
            case "ACTIVE" -> BigDecimal.valueOf(1.725);
            case "VERY_ACTIVE" -> BigDecimal.valueOf(1.9);
            default -> BigDecimal.valueOf(1.55);
        };
    }
}
