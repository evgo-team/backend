package com.project.mealplan.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.dtos.mealplan.request.UpdateMealSlotRecipeRequest;
import com.project.mealplan.dtos.mealplan.response.MealDayResponse;
import com.project.mealplan.dtos.mealplan.response.MealSlotDetailResponse;
import com.project.mealplan.dtos.mealplan.response.MealSlotListResponse;
import com.project.mealplan.dtos.mealplan.response.MealSlotResponse;
import com.project.mealplan.dtos.mealplan.response.NutritionDetailResponse;
import com.project.mealplan.dtos.mealplan.response.NutritionSummaryResponse;
import com.project.mealplan.dtos.mealplan.response.UpdatedMealSlotResponse;
import com.project.mealplan.dtos.mealplan.response.WeeklyMealPlanResponse;
import com.project.mealplan.entity.*;
import com.project.mealplan.repository.*;
import com.project.mealplan.service.MealPlanService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealPlanServiceImpl implements MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final PantryRepository pantryRepository;
    private final MealSlotRepository mealSlotRepository;

    // Scoring weights
    private static final double CALORIE_WEIGHT = 0.5;
    private static final double PANTRY_WEIGHT = 0.3;
    private static final double FAVORITE_WEIGHT = 0.2;

    // Variety penalty for recipe repetition
    private static final double BASE_REPETITION_PENALTY = 0.7; // 70% penalty per overall use
    private static final double RECENT_USE_PENALTY = 0.9; // 100% penalty if used in last day
    private static final double RANDOMIZATION_FACTOR = 0.05; // 5% random variance to break ties
    private static final int RECENT_USE_WINDOW = 3; // Consider last 3 days for recent use
    private static final int MAX_USES_PER_MEAL_TYPE = 2; // Hard limit per meal type
    private final Random random = new Random();

    @Override
    @Transactional
    public WeeklyMealPlanResponse generateWeeklyMealPlan(Long userId, LocalDate startDate) {
        log.info("Generating weekly meal plan for user: {}, startDate: {}", userId, startDate);

        // 1. Calculate week range (Monday to Sunday)
        LocalDate weekStart = calculateWeekStart(startDate);
        LocalDate weekEnd = weekStart.plusDays(6);

        log.debug("Week range: {} to {}", weekStart, weekEnd);

        // 2. Check if meal plan already exists
        mealPlanRepository.findByUser_UserIdAndStartDate(userId, weekStart)
                .ifPresent(existing -> {
                    throw new AppException(ErrorCode.MEAL_PLAN_ALREADY_EXISTS);
                });

        // 3. Get user and validate profile
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        validateUserProfile(user);

        // 4. Calculate daily calorie target
        BigDecimal dailyCalorieTarget = calculateDailyCalorieTarget(user);
        log.info("Daily calorie target: {}", dailyCalorieTarget);

        // 5. Get user's pantry and favorites
        Set<Long> pantryIngredientIds = getUserPantryIngredientIds(userId);
        Set<Long> favoriteRecipeIds = user.getFavorites().stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toSet());

        // 6. Get all published recipes
        List<Recipe> availableRecipes = recipeRepository.findAll().stream()
                .filter(r -> r.getStatus() == RecipeStatus.PUBLISHED)
                .collect(Collectors.toList());

        if (availableRecipes.isEmpty()) {
            throw new AppException(ErrorCode.NO_RECIPES_AVAILABLE);
        }

        // 7. Create meal plan entity
        MealPlan mealPlan = new MealPlan();
        mealPlan.setUser(user);
        mealPlan.setStartDate(weekStart);
        mealPlan.setEndDate(weekEnd);
        mealPlan.setGoal("Auto-generated weekly meal plan");

        // 8. Generate 7 days with meal slots
        List<MealDayResponse> dayResponses = new ArrayList<>();
        Map<Long, Double> recipeScores = new HashMap<>(); // Cache for recipe scores
        Map<Long, Integer> usedRecipes = new HashMap<>(); // Track overall recipe usage
        Map<String, Integer> perMealTypeUsage = new HashMap<>(); // Track usage per meal type (key: recipeId_mealType)
        List<Map<MealType, Long>> recentlyUsedRecipes = new ArrayList<>(); // Track recent use per day per meal type

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            MealDay mealDay = new MealDay();
            mealDay.setDate(currentDate);
            mealPlan.addMealDay(mealDay);

            // Track today's selections for recent use tracking
            Map<MealType, Long> todaySelections = new EnumMap<>(MealType.class);

            // Generate meals for each type
            Map<MealType, List<MealSlotResponse>> mealsMap = new EnumMap<>(MealType.class);
            BigDecimal dailyCalories = BigDecimal.ZERO;
            BigDecimal dailyProtein = BigDecimal.ZERO;
            BigDecimal dailyCarbs = BigDecimal.ZERO;
            BigDecimal dailyFat = BigDecimal.ZERO;

            for (MealType mealType : MealType.values()) {
                // Calculate target calories for this meal type
                BigDecimal mealCalorieTarget = dailyCalorieTarget.divide(
                        BigDecimal.valueOf(4), 2, RoundingMode.HALF_UP);

                // Select best recipe for this meal type
                Recipe selectedRecipe = selectBestRecipe(
                        availableRecipes,
                        mealType,
                        mealCalorieTarget,
                        pantryIngredientIds,
                        favoriteRecipeIds,
                        recipeScores,
                        usedRecipes,
                        perMealTypeUsage,
                        recentlyUsedRecipes,
                        i); // Pass current day index

                if (selectedRecipe != null) {
                    MealSlot mealSlot = new MealSlot();
                    mealSlot.setType(mealType);
                    mealSlot.setRecipe(selectedRecipe);
                    mealSlot.setQuantity(1.0);
                    mealDay.addMealSlot(mealSlot);

                    // Track recipe usage for variety
                    usedRecipes.merge(selectedRecipe.getRecipeId(), 1, Integer::sum);
                    todaySelections.put(mealType, selectedRecipe.getRecipeId());

                    // Track per-meal-type usage
                    String usageKey = selectedRecipe.getRecipeId() + "_" + mealType;
                    perMealTypeUsage.merge(usageKey, 1, Integer::sum);

                    // Calculate score
                    double score = recipeScores.computeIfAbsent(selectedRecipe.getRecipeId(),
                            id -> calculateRecipeScore(selectedRecipe, mealCalorieTarget,
                                    pantryIngredientIds, favoriteRecipeIds));

                    // Create response
                    MealSlotResponse slotResponse = MealSlotResponse.builder()
                            .mealSlotId(null) // Will be set after save
                            .recipeId(selectedRecipe.getRecipeId())
                            .title(selectedRecipe.getTitle())
                            .calories(selectedRecipe.getCalories())
                            .score(score)
                            .build();

                    mealsMap.computeIfAbsent(mealType, k -> new ArrayList<>()).add(slotResponse);

                    // Accumulate nutrition
                    if (selectedRecipe.getCalories() != null) {
                        dailyCalories = dailyCalories.add(selectedRecipe.getCalories());
                    }

                    // Get nutrition from recipe ingredients
                    Map<String, BigDecimal> nutrition = calculateRecipeNutrition(selectedRecipe);
                    dailyProtein = dailyProtein.add(nutrition.getOrDefault("protein", BigDecimal.ZERO));
                    dailyCarbs = dailyCarbs.add(nutrition.getOrDefault("carbs", BigDecimal.ZERO));
                    dailyFat = dailyFat.add(nutrition.getOrDefault("fat", BigDecimal.ZERO));
                }
            }

            // Create nutrition summary
            NutritionSummaryResponse nutritionSummary = NutritionSummaryResponse.builder()
                    .totalCalories(dailyCalories)
                    .protein(dailyProtein)
                    .carbs(dailyCarbs)
                    .fat(dailyFat)
                    .build();

            // Create day response
            MealDayResponse dayResponse = MealDayResponse.builder()
                    .date(currentDate)
                    .meals(mealsMap)
                    .nutritionSummary(nutritionSummary)
                    .build();

            dayResponses.add(dayResponse);

            // Store today's selections for recent-use tracking
            recentlyUsedRecipes.add(todaySelections);
        }

        // 9. Save meal plan
        MealPlan savedMealPlan = mealPlanRepository.save(mealPlan);

        // Update meal slot IDs in responses
        for (int i = 0; i < savedMealPlan.getMealDays().size(); i++) {
            MealDay mealDay = savedMealPlan.getMealDays().get(i);
            MealDayResponse dayResponse = dayResponses.get(i);

            int slotIndex = 0;
            for (MealType mealType : MealType.values()) {
                List<MealSlotResponse> slots = dayResponse.getMeals().get(mealType);
                if (slots != null) {
                    for (MealSlotResponse slot : slots) {
                        if (slotIndex < mealDay.getMealSlots().size()) {
                            slot.setMealSlotId(mealDay.getMealSlots().get(slotIndex).getId());
                            slotIndex++;
                        }
                    }
                }
            }
        }

        // 10. Build response
        return WeeklyMealPlanResponse.builder()
                .weekStartDate(weekStart)
                .weekEndDate(weekEnd)
                .days(dayResponses)
                .build();
    }

    /**
     * Calculate the Monday of the week for the given date
     */
    private LocalDate calculateWeekStart(LocalDate date) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        DayOfWeek dayOfWeek = targetDate.getDayOfWeek();
        return targetDate.minusDays(dayOfWeek.getValue() - 1);
    }

    /**
     * Validate user profile has required fields for meal planning
     */
    private void validateUserProfile(User user) {
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null) {
            throw new AppException(ErrorCode.INSUFFICIENT_USER_PROFILE);
        }
    }

    /**
     * Calculate daily calorie target using Mifflin-St Jeor equation
     */
    private BigDecimal calculateDailyCalorieTarget(User user) {
        BigDecimal weight = user.getWeight(); // kg
        BigDecimal height = user.getHeight(); // cm
        int age = user.getAge();

        // BMR calculation
        BigDecimal bmr;
        if (user.getGender() != null && user.getGender().toString().equalsIgnoreCase("MALE")) {
            // BMR (men) = 10 * weight + 6.25 * height - 5 * age + 5
            bmr = weight.multiply(BigDecimal.TEN)
                    .add(height.multiply(BigDecimal.valueOf(6.25)))
                    .subtract(BigDecimal.valueOf(age * 5))
                    .add(BigDecimal.valueOf(5));
        } else {
            // BMR (women) = 10 * weight + 6.25 * height - 5 * age - 161
            bmr = weight.multiply(BigDecimal.TEN)
                    .add(height.multiply(BigDecimal.valueOf(6.25)))
                    .subtract(BigDecimal.valueOf(age * 5))
                    .subtract(BigDecimal.valueOf(161));
        }

        // Activity multiplier (default to moderate = 1.55)
        BigDecimal activityMultiplier = getActivityMultiplier(user.getActivityLevel().name());
        BigDecimal tdee = bmr.multiply(activityMultiplier);

        return tdee.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Get activity multiplier based on activity level
     */
    private BigDecimal getActivityMultiplier(String activityLevel) {
        if (activityLevel == null) {
            return BigDecimal.valueOf(1.55); // Moderate
        }

        return switch (activityLevel.toLowerCase()) {
            case "sedentary" -> BigDecimal.valueOf(1.2);
            case "light" -> BigDecimal.valueOf(1.375);
            case "moderate" -> BigDecimal.valueOf(1.55);
            case "active" -> BigDecimal.valueOf(1.725);
            case "very_active" -> BigDecimal.valueOf(1.9);
            default -> BigDecimal.valueOf(1.55);
        };
    }

    /**
     * Get ingredient IDs from user's pantry
     */
    private Set<Long> getUserPantryIngredientIds(Long userId) {
        return pantryRepository.findByUser_UserId(userId)
                .map(pantry -> pantry.getItems().stream()
                        .map(item -> item.getIngredient().getId())
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    /**
     * Select best recipe with advanced variety tracking and hard limits:
     * - HARD LIMIT: Max 2 uses per recipe per meal type per week
     * - Base repetition penalty for any reuse
     * - Heavy penalty for recent use (within last 3 days)
     * - Exponential decay for recent penalties
     * - Randomization to break ties and add unpredictability
     */
    private Recipe selectBestRecipe(
            List<Recipe> availableRecipes,
            MealType mealType,
            BigDecimal targetCalories,
            Set<Long> pantryIngredientIds,
            Set<Long> favoriteRecipeIds,
            Map<Long, Double> scoreCache,
            Map<Long, Integer> usedRecipes,
            Map<String, Integer> perMealTypeUsage,
            List<Map<MealType, Long>> recentlyUsedRecipes,
            int currentDayIndex) {

        return availableRecipes.stream()
                .filter(recipe -> {
                    // Filter by meal type
                    if (recipe.getMealType() != mealType) {
                        return false;
                    }

                    // HARD LIMIT: Filter out recipes that hit the limit for this meal type
                    String usageKey = recipe.getRecipeId() + "_" + mealType;
                    int mealTypeUsage = perMealTypeUsage.getOrDefault(usageKey, 0);
                    return mealTypeUsage < MAX_USES_PER_MEAL_TYPE;
                })
                .max(Comparator.comparingDouble(recipe -> {
                    Long recipeId = recipe.getRecipeId();

                    // 1. Get base quality score (0.0 - 1.0)
                    double baseScore = scoreCache.computeIfAbsent(recipeId,
                            id -> calculateRecipeScore(recipe, targetCalories,
                                    pantryIngredientIds, favoriteRecipeIds));

                    // 2. Apply base repetition penalty (linear)
                    int totalUsageCount = usedRecipes.getOrDefault(recipeId, 0);
                    double repetitionPenalty = totalUsageCount * BASE_REPETITION_PENALTY;

                    // 3. Apply recent use penalty (exponential decay)
                    double recentUsePenalty = 0.0;
                    int lookbackDays = Math.min(currentDayIndex, RECENT_USE_WINDOW);
                    for (int daysAgo = 1; daysAgo <= lookbackDays; daysAgo++) {
                        int dayIndex = currentDayIndex - daysAgo;
                        if (dayIndex >= 0 && dayIndex < recentlyUsedRecipes.size()) {
                            Map<MealType, Long> dayRecipes = recentlyUsedRecipes.get(dayIndex);
                            Long usedRecipeId = dayRecipes.get(mealType);
                            if (recipeId.equals(usedRecipeId)) {
                                // Exponential decay: most recent day has highest penalty
                                double decayFactor = Math.pow(0.5, daysAgo - 1); // 100%, 50%, 25%...
                                recentUsePenalty += RECENT_USE_PENALTY * decayFactor;
                            }
                        }
                    }

                    // 4. Add small random variance to break ties and add unpredictability
                    double randomVariance = (random.nextDouble() - 0.5) * RANDOMIZATION_FACTOR;

                    // 5. Calculate final adjusted score
                    double adjustedScore = baseScore - repetitionPenalty - recentUsePenalty + randomVariance;

                    return Math.max(0.0, adjustedScore); // Ensure non-negative
                }))
                .orElse(null);
    }

    /**
     * Calculate recipe score based on calorie fit, pantry match, and favorite
     * status
     */
    private double calculateRecipeScore(
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

        // Total score
        double totalScore = CALORIE_WEIGHT * calorieScore
                + PANTRY_WEIGHT * pantryScore
                + FAVORITE_WEIGHT * favoriteScore;

        return Math.max(0.0, Math.min(1.0, totalScore)); // Clamp to [0, 1]
    }

    /**
     * Calculate calorie fit score
     */
    private double calculateCalorieScore(BigDecimal recipeCalories, BigDecimal targetCalories) {
        if (recipeCalories == null || targetCalories.compareTo(BigDecimal.ZERO) == 0) {
            return 0.5; // Neutral score
        }

        BigDecimal diff = recipeCalories.subtract(targetCalories).abs();
        BigDecimal ratio = diff.divide(targetCalories, 4, RoundingMode.HALF_UP);

        double score = 1.0 - ratio.doubleValue();
        return Math.max(0.0, Math.min(1.0, score));
    }

    /**
     * Calculate pantry match score
     */
    private double calculatePantryScore(Recipe recipe, Set<Long> pantryIngredientIds) {
        if (recipe.getIngredients().isEmpty()) {
            return 0.0;
        }

        long matchedCount = recipe.getIngredients().stream()
                .filter(ri -> pantryIngredientIds.contains(ri.getIngredient().getId()))
                .count();

        return (double) matchedCount / recipe.getIngredients().size();
    }

    /**
     * Calculate nutrition for a recipe
     */
    private Map<String, BigDecimal> calculateRecipeNutrition(Recipe recipe) {
        Map<String, BigDecimal> nutrition = new HashMap<>();
        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal carbs = BigDecimal.ZERO;
        BigDecimal fat = BigDecimal.ZERO;

        for (RecipeIngredient ri : recipe.getIngredients()) {
            Ingredient ingredient = ri.getIngredient();
            Double quantity = ri.getQuantity() != null ? ri.getQuantity() : 100.0;

            for (IngredientNutrition in : ingredient.getNutritions()) {
                String nutritionName = in.getNutritionType().getName().toLowerCase();
                BigDecimal amount = in.getAmountPer100g()
                        .multiply(BigDecimal.valueOf(quantity))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                if (nutritionName.contains("protein")) {
                    protein = protein.add(amount);
                } else if (nutritionName.contains("carb")) {
                    carbs = carbs.add(amount);
                } else if (nutritionName.contains("fat")) {
                    fat = fat.add(amount);
                }
            }
        }

        nutrition.put("protein", protein);
        nutrition.put("carbs", carbs);
        nutrition.put("fat", fat);

        return nutrition;
    }

    @Override
    @Transactional(readOnly = true)
    public MealSlotListResponse getMealSlotsByDateAndMealType(Long userId, LocalDate date, MealType mealType) {
        log.info("Getting meal slots for user: {}, date: {}, mealType: {}", userId, date, mealType);

        LocalDate weekStart = calculateWeekStart(date);

        MealPlan mealPlan = mealPlanRepository.findByUser_UserIdAndStartDate(userId, weekStart)
                .orElse(null);

        MealSlotListResponse response = MealSlotListResponse.builder()
                .date(date)
                .mealType(mealType)
                .slots(new ArrayList<>())
                .nutritionSummary(NutritionSummaryResponse.builder()
                        .totalCalories(BigDecimal.ZERO)
                        .protein(BigDecimal.ZERO)
                        .carbs(BigDecimal.ZERO)
                        .fat(BigDecimal.ZERO)
                        .build())
                .build();

        if (mealPlan == null) {
            log.debug("No meal plan found for user: {}, weekStart: {}", userId, weekStart);
            return response;
        }

        MealDay mealDay = mealPlan.getMealDays().stream()
                .filter(md -> md.getDate().equals(date))
                .findFirst()
                .orElse(null);

        if (mealDay == null) {
            log.debug("No meal day found for date: {}", date);
            return response;
        }

        List<MealSlot> mealSlots = mealDay.getMealSlots().stream()
                .filter(slot -> slot.getType() == mealType)
                .collect(Collectors.toList());

        List<MealSlotDetailResponse> slots = new ArrayList<>();
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;

        for (MealSlot mealSlot : mealSlots) {
            Recipe recipe = mealSlot.getRecipe();
            Map<String, BigDecimal> nutrition = calculateRecipeNutrition(recipe);

            MealSlotDetailResponse slotResponse = MealSlotDetailResponse.builder()
                    .mealSlotId(mealSlot.getId())
                    .recipeId(recipe.getRecipeId())
                    .title(recipe.getTitle())
                    .imageUrl(recipe.getImageUrl())
                    .calories(recipe.getCalories())
                    .nutrition(NutritionDetailResponse.builder()
                            .protein(nutrition.getOrDefault("protein", BigDecimal.ZERO))
                            .carbs(nutrition.getOrDefault("carbs", BigDecimal.ZERO))
                            .fat(nutrition.getOrDefault("fat", BigDecimal.ZERO))
                            .build())
                    .build();

            slots.add(slotResponse);

            if (recipe.getCalories() != null) {
                totalCalories = totalCalories.add(recipe.getCalories());
            }
            totalProtein = totalProtein.add(nutrition.getOrDefault("protein", BigDecimal.ZERO));
            totalCarbs = totalCarbs.add(nutrition.getOrDefault("carbs", BigDecimal.ZERO));
            totalFat = totalFat.add(nutrition.getOrDefault("fat", BigDecimal.ZERO));
        }

        response.setSlots(slots);
        response.setNutritionSummary(NutritionSummaryResponse.builder()
                .totalCalories(totalCalories)
                .protein(totalProtein)
                .carbs(totalCarbs)
                .fat(totalFat)
                .build());

        log.info("Retrieved {} meal slots for date: {}, mealType: {}", slots.size(), date, mealType);

        return response;
    }

    @Override
    @Transactional
    public UpdatedMealSlotResponse updateMealSlotRecipe(Long userId, Long mealSlotId,
            UpdateMealSlotRecipeRequest request) {
        log.info("Updating meal slot: {} for user: {} with recipe: {}", mealSlotId, userId, request.getRecipeId());

        MealSlot mealSlot = mealSlotRepository.findById(mealSlotId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Meal slot not found"));

        MealDay mealDay = mealSlot.getMealDay();
        MealPlan mealPlan = mealDay.getMealPlan();

        if (!mealPlan.getUser().getUserId().equals(userId)) {
            log.warn("Unauthorized access attempt: user {} trying to update slot of user {}",
                    userId, mealPlan.getUser().getUserId());
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        Recipe newRecipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        mealSlot.setRecipe(newRecipe);
        MealSlot updatedMealSlot = mealSlotRepository.save(mealSlot);
        log.info("Meal slot {} updated successfully with recipe {}", mealSlotId, request.getRecipeId());

        NutritionSummaryResponse dayNutrition = calculateDayNutritionSummary(mealDay);

        Map<String, BigDecimal> recipeNutrition = calculateRecipeNutrition(newRecipe);

        UpdatedMealSlotResponse response = UpdatedMealSlotResponse.builder()
                .mealSlotId(updatedMealSlot.getId())
                .date(mealDay.getDate().toString())
                .mealType(updatedMealSlot.getType())
                .recipe(UpdatedMealSlotResponse.RecipeDetailResponse.builder()
                        .recipeId(newRecipe.getRecipeId())
                        .title(newRecipe.getTitle())
                        .imageUrl(newRecipe.getImageUrl())
                        .calories(newRecipe.getCalories())
                        .nutrition(NutritionSummaryResponse.builder()
                                .protein(recipeNutrition.getOrDefault("protein", BigDecimal.ZERO))
                                .carbs(recipeNutrition.getOrDefault("carbs", BigDecimal.ZERO))
                                .fat(recipeNutrition.getOrDefault("fat", BigDecimal.ZERO))
                                .totalCalories(
                                        newRecipe.getCalories() != null ? newRecipe.getCalories() : BigDecimal.ZERO)
                                .build())
                        .build())
                .nutritionSummaryOfDay(dayNutrition)
                .build();

        return response;
    }

    private NutritionSummaryResponse calculateDayNutritionSummary(MealDay mealDay) {
        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;

        for (MealSlot mealSlot : mealDay.getMealSlots()) {
            Recipe recipe = mealSlot.getRecipe();

            if (recipe.getCalories() != null) {
                totalCalories = totalCalories.add(recipe.getCalories());
            }

            Map<String, BigDecimal> nutrition = calculateRecipeNutrition(recipe);
            totalProtein = totalProtein.add(nutrition.getOrDefault("protein", BigDecimal.ZERO));
            totalCarbs = totalCarbs.add(nutrition.getOrDefault("carbs", BigDecimal.ZERO));
            totalFat = totalFat.add(nutrition.getOrDefault("fat", BigDecimal.ZERO));
        }

        return NutritionSummaryResponse.builder()
                .totalCalories(totalCalories)
                .protein(totalProtein)
                .carbs(totalCarbs)
                .fat(totalFat)
                .build();
    }
}
