package com.project.mealplan.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mealplan.common.enums.ActivityLevel;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.common.util.NutritionCalculator;
import com.project.mealplan.dtos.nutrition.request.LogFoodRequest;
import com.project.mealplan.dtos.nutrition.request.SetNutritionGoalRequest;
import com.project.mealplan.dtos.nutrition.response.DailyFoodLogsResponse;
import com.project.mealplan.dtos.nutrition.response.DailyNutritionResponse;
import com.project.mealplan.dtos.nutrition.response.FoodLogResponse;
import com.project.mealplan.dtos.nutrition.response.NutritionGoalResponse;
import com.project.mealplan.dtos.nutrition.response.WeeklyNutritionSummaryResponse;
import com.project.mealplan.entity.DailyNutritionLog;
import com.project.mealplan.entity.FoodLog;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.IngredientNutrition;
import com.project.mealplan.entity.MealDay;
import com.project.mealplan.entity.MealSlot;
import com.project.mealplan.entity.NutritionGoal;
import com.project.mealplan.entity.Recipe;
import com.project.mealplan.entity.RecipeIngredient;
import com.project.mealplan.entity.User;
import com.project.mealplan.dtos.nutrition.response.MealConsumedResponse;
import com.project.mealplan.repository.DailyNutritionLogRepository;
import com.project.mealplan.repository.FoodLogRepository;
import com.project.mealplan.repository.MealDayRepository;
import com.project.mealplan.repository.MealSlotRepository;
import com.project.mealplan.repository.NutritionGoalRepository;
import com.project.mealplan.repository.RecipeRepository;
import com.project.mealplan.repository.UserRepository;
import com.project.mealplan.service.NutritionTrackingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NutritionTrackingServiceImpl implements NutritionTrackingService {

        private final NutritionGoalRepository nutritionGoalRepository;
        private final DailyNutritionLogRepository dailyNutritionLogRepository;
        private final UserRepository userRepository;
        private final MealDayRepository mealDayRepository;
        private final MealSlotRepository mealSlotRepository;
        private final FoodLogRepository foodLogRepository;
        private final RecipeRepository recipeRepository;

        @Override
        @Transactional
        public NutritionGoalResponse setNutritionGoal(Long userId, SetNutritionGoalRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                NutritionGoal goal = nutritionGoalRepository.findByUser_UserId(userId)
                                .orElse(NutritionGoal.builder().user(user).build());

                goal.setDailyCalories(request.getDailyCalories());
                goal.setDailyProtein(request.getDailyProtein());
                goal.setDailyCarbs(request.getDailyCarbs());
                goal.setDailyFat(request.getDailyFat());

                NutritionGoal saved = nutritionGoalRepository.save(goal);

                log.info("User {} updated nutrition goals: calories={}, protein={}, carbs={}, fat={}",
                                userId, request.getDailyCalories(), request.getDailyProtein(),
                                request.getDailyCarbs(), request.getDailyFat());

                return mapToNutritionGoalResponse(saved);
        }

        @Override
        public NutritionGoalResponse getNutritionGoal(Long userId) {
                return nutritionGoalRepository.findByUser_UserId(userId)
                                .map(this::mapToNutritionGoalResponse)
                                .orElse(null);
        }

        @Override
        public NutritionGoalResponse calculateDefaultGoal(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                // Validate user profile has required fields
                if (user.getWeight() == null || user.getHeight() == null ||
                                user.getAge() == null || user.getGender() == null) {
                        throw new AppException(ErrorCode.INSUFFICIENT_USER_PROFILE);
                }

                // Calculate BMR using Mifflin-St Jeor equation
                BigDecimal bmr = NutritionCalculator.calculateBMR(
                                user.getWeight(), user.getHeight(), user.getAge(), user.getGender());

                // Apply activity level multiplier for TDEE
                ActivityLevel activityLevel = user.getActivityLevel() != null
                                ? user.getActivityLevel()
                                : ActivityLevel.SEDENTARY;
                BigDecimal tdee = NutritionCalculator.calculateTDEE(bmr, activityLevel);

                // Calculate macro distribution
                BigDecimal dailyProtein = NutritionCalculator.calculateDailyProtein(tdee);
                BigDecimal dailyCarbs = NutritionCalculator.calculateDailyCarbs(tdee);
                BigDecimal dailyFat = NutritionCalculator.calculateDailyFat(tdee);

                log.info("Calculated default goals for user {}: BMR={}, TDEE={}, protein={}, carbs={}, fat={}",
                                userId, bmr, tdee, dailyProtein, dailyCarbs, dailyFat);

                return NutritionGoalResponse.builder()
                                .dailyCalories(tdee)
                                .dailyProtein(dailyProtein)
                                .dailyCarbs(dailyCarbs)
                                .dailyFat(dailyFat)
                                .build();
        }

        @Override
        public DailyNutritionResponse getDailyNutrition(Long userId, LocalDate date) {
                // Get or calculate daily nutrition
                Map<String, BigDecimal> consumed = calculateDailyConsumed(userId, date);

                // Get goals (or use defaults)
                NutritionGoalResponse goals = getNutritionGoal(userId);
                if (goals == null) {
                        try {
                                goals = calculateDefaultGoal(userId);
                        } catch (AppException e) {
                                // Profile incomplete, use zero goals
                                goals = NutritionGoalResponse.builder()
                                                .dailyCalories(BigDecimal.ZERO)
                                                .dailyProtein(BigDecimal.ZERO)
                                                .dailyCarbs(BigDecimal.ZERO)
                                                .dailyFat(BigDecimal.ZERO)
                                                .build();
                        }
                }

                return DailyNutritionResponse.builder()
                                .date(date)
                                .consumedCalories(consumed.get("calories"))
                                .consumedProtein(consumed.get("protein"))
                                .consumedCarbs(consumed.get("carbs"))
                                .consumedFat(consumed.get("fat"))
                                .goalCalories(goals.getDailyCalories())
                                .goalProtein(goals.getDailyProtein())
                                .goalCarbs(goals.getDailyCarbs())
                                .goalFat(goals.getDailyFat())
                                .caloriesProgress(NutritionCalculator.calculateProgress(
                                                consumed.get("calories"), goals.getDailyCalories()))
                                .proteinProgress(NutritionCalculator.calculateProgress(
                                                consumed.get("protein"), goals.getDailyProtein()))
                                .carbsProgress(NutritionCalculator.calculateProgress(
                                                consumed.get("carbs"), goals.getDailyCarbs()))
                                .fatProgress(NutritionCalculator.calculateProgress(
                                                consumed.get("fat"), goals.getDailyFat()))
                                .build();
        }

        @Override
        public WeeklyNutritionSummaryResponse getWeeklyNutritionSummary(Long userId, LocalDate startDate) {
                LocalDate endDate = startDate.plusDays(6);

                List<DailyNutritionResponse> dailyData = new ArrayList<>();
                BigDecimal totalCalories = BigDecimal.ZERO;
                BigDecimal totalProtein = BigDecimal.ZERO;
                BigDecimal totalCarbs = BigDecimal.ZERO;
                BigDecimal totalFat = BigDecimal.ZERO;
                int daysWithData = 0;

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        DailyNutritionResponse dailyNutrition = getDailyNutrition(userId, date);
                        dailyData.add(dailyNutrition);

                        if (dailyNutrition.getConsumedCalories() != null &&
                                        dailyNutrition.getConsumedCalories().compareTo(BigDecimal.ZERO) > 0) {
                                totalCalories = totalCalories.add(dailyNutrition.getConsumedCalories());
                                totalProtein = totalProtein.add(dailyNutrition.getConsumedProtein());
                                totalCarbs = totalCarbs.add(dailyNutrition.getConsumedCarbs());
                                totalFat = totalFat.add(dailyNutrition.getConsumedFat());
                                daysWithData++;
                        }
                }

                // Calculate averages
                BigDecimal divisor = daysWithData > 0 ? new BigDecimal(daysWithData) : BigDecimal.ONE;
                BigDecimal avgCalories = totalCalories.divide(divisor, 2, RoundingMode.HALF_UP);
                BigDecimal avgProtein = totalProtein.divide(divisor, 2, RoundingMode.HALF_UP);
                BigDecimal avgCarbs = totalCarbs.divide(divisor, 2, RoundingMode.HALF_UP);
                BigDecimal avgFat = totalFat.divide(divisor, 2, RoundingMode.HALF_UP);

                // Get goals for reference
                NutritionGoalResponse goals = getNutritionGoal(userId);
                if (goals == null) {
                        try {
                                goals = calculateDefaultGoal(userId);
                        } catch (AppException e) {
                                goals = NutritionGoalResponse.builder()
                                                .dailyCalories(BigDecimal.ZERO)
                                                .dailyProtein(BigDecimal.ZERO)
                                                .dailyCarbs(BigDecimal.ZERO)
                                                .dailyFat(BigDecimal.ZERO)
                                                .build();
                        }
                }

                return WeeklyNutritionSummaryResponse.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .dailyData(dailyData)
                                .avgCalories(avgCalories)
                                .avgProtein(avgProtein)
                                .avgCarbs(avgCarbs)
                                .avgFat(avgFat)
                                .goalCalories(goals.getDailyCalories())
                                .goalProtein(goals.getDailyProtein())
                                .goalCarbs(goals.getDailyCarbs())
                                .goalFat(goals.getDailyFat())
                                .avgCaloriesProgress(NutritionCalculator.calculateProgress(
                                                avgCalories, goals.getDailyCalories()))
                                .avgProteinProgress(NutritionCalculator.calculateProgress(
                                                avgProtein, goals.getDailyProtein()))
                                .avgCarbsProgress(NutritionCalculator.calculateProgress(
                                                avgCarbs, goals.getDailyCarbs()))
                                .avgFatProgress(NutritionCalculator.calculateProgress(
                                                avgFat, goals.getDailyFat()))
                                .build();
        }

        // ==================== Private Helper Methods ====================

        private Map<String, BigDecimal> calculateDailyConsumed(Long userId, LocalDate date) {
                Map<String, BigDecimal> result = new HashMap<>();
                result.put("calories", BigDecimal.ZERO);
                result.put("protein", BigDecimal.ZERO);
                result.put("carbs", BigDecimal.ZERO);
                result.put("fat", BigDecimal.ZERO);

                // Check if we have cached data
                Optional<DailyNutritionLog> cachedLog = dailyNutritionLogRepository
                                .findByUser_UserIdAndDate(userId, date);

                if (cachedLog.isPresent()) {
                        DailyNutritionLog log = cachedLog.get();
                        result.put("calories",
                                        log.getTotalCalories() != null ? log.getTotalCalories() : BigDecimal.ZERO);
                        result.put("protein", log.getTotalProtein() != null ? log.getTotalProtein() : BigDecimal.ZERO);
                        result.put("carbs", log.getTotalCarbs() != null ? log.getTotalCarbs() : BigDecimal.ZERO);
                        result.put("fat", log.getTotalFat() != null ? log.getTotalFat() : BigDecimal.ZERO);
                        return result;
                }

                // Calculate from Food Logs only (includes both ad-hoc and consumed meal plan
                // entries)
                // Note: Consumed meals from meal plan now create FoodLog entries automatically
                BigDecimal totalCalories = BigDecimal.ZERO;
                BigDecimal totalProtein = BigDecimal.ZERO;
                BigDecimal totalCarbs = BigDecimal.ZERO;
                BigDecimal totalFat = BigDecimal.ZERO;

                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                List<FoodLog> foodLogs = foodLogRepository.findByUser_UserIdAndConsumeDateBetweenOrderByConsumeDateAsc(
                                userId,
                                startOfDay, endOfDay);

                for (FoodLog foodLog : foodLogs) {
                        Recipe recipe = foodLog.getRecipe();
                        BigDecimal quantity = foodLog.getQuantity() != null ? foodLog.getQuantity() : BigDecimal.ONE;

                        if (recipe.getCalories() != null) {
                                totalCalories = totalCalories.add(recipe.getCalories().multiply(quantity));
                        }

                        Map<String, BigDecimal> nutrition = calculateRecipeNutrition(recipe);
                        totalProtein = totalProtein
                                        .add(nutrition.getOrDefault("protein", BigDecimal.ZERO).multiply(quantity));
                        totalCarbs = totalCarbs
                                        .add(nutrition.getOrDefault("carbs", BigDecimal.ZERO).multiply(quantity));
                        totalFat = totalFat.add(nutrition.getOrDefault("fat", BigDecimal.ZERO).multiply(quantity));
                }

                // Cache the result
                User user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                        DailyNutritionLog logEntry = DailyNutritionLog.builder()
                                        .user(user)
                                        .date(date)
                                        .totalCalories(totalCalories)
                                        .totalProtein(totalProtein)
                                        .totalCarbs(totalCarbs)
                                        .totalFat(totalFat)
                                        .build();
                        dailyNutritionLogRepository.save(logEntry);
                }

                result.put("calories", totalCalories);
                result.put("protein", totalProtein);
                result.put("carbs", totalCarbs);
                result.put("fat", totalFat);

                return result;
        }

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

        private NutritionGoalResponse mapToNutritionGoalResponse(NutritionGoal goal) {
                return NutritionGoalResponse.builder()
                                .id(goal.getId())
                                .dailyCalories(goal.getDailyCalories())
                                .dailyProtein(goal.getDailyProtein())
                                .dailyCarbs(goal.getDailyCarbs())
                                .dailyFat(goal.getDailyFat())
                                .build();
        }

        @Override
        @Transactional
        public MealConsumedResponse logMealConsumed(Long userId, Long mealSlotId, Boolean consumed) {
                // Find meal slot and verify ownership
                MealSlot mealSlot = mealSlotRepository.findByIdAndMealDay_MealPlan_User_UserId(mealSlotId, userId)
                                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,
                                                "Meal slot not found or access denied"));

                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                // Update consumed status
                boolean wasConsumed = Boolean.TRUE.equals(mealSlot.getConsumed());
                boolean isNowConsumed = Boolean.TRUE.equals(consumed);

                mealSlot.setConsumed(isNowConsumed);

                LocalDateTime consumedAt = null;
                if (isNowConsumed && !wasConsumed) {
                        consumedAt = LocalDateTime.now();
                        mealSlot.setConsumedAt(consumedAt);
                } else if (!isNowConsumed) {
                        mealSlot.setConsumedAt(null);
                } else {
                        consumedAt = mealSlot.getConsumedAt();
                }

                mealSlotRepository.save(mealSlot);

                Recipe recipe = mealSlot.getRecipe();
                LocalDate date = mealSlot.getMealDay().getDate();

                // Sync to FoodLog
                if (isNowConsumed && !wasConsumed) {
                        // Create FoodLog entry when marking as consumed
                        FoodLog foodLog = FoodLog.builder()
                                        .user(user)
                                        .recipe(recipe)
                                        .consumeDate(consumedAt != null ? consumedAt : date.atStartOfDay())
                                        .quantity(BigDecimal.ONE)
                                        .mealSlotId(mealSlotId)
                                        .build();
                        foodLogRepository.save(foodLog);
                        log.info("Created FoodLog for meal slot {} (recipe: {})", mealSlotId, recipe.getTitle());
                } else if (!isNowConsumed && wasConsumed) {
                        // Delete FoodLog entry when un-marking
                        foodLogRepository.findByMealSlotIdAndUser_UserId(mealSlotId, userId)
                                        .ifPresent(foodLog -> {
                                                foodLogRepository.delete(foodLog);
                                                log.info("Deleted FoodLog for meal slot {}", mealSlotId);
                                        });
                }

                // Invalidate cached nutrition log for this date
                dailyNutritionLogRepository.findByUser_UserIdAndDate(userId, date)
                                .ifPresent(dailyNutritionLogRepository::delete);

                log.info("User {} marked meal slot {} as consumed={}", userId, mealSlotId, isNowConsumed);

                return MealConsumedResponse.builder()
                                .mealSlotId(mealSlotId)
                                .recipeId(recipe.getRecipeId())
                                .recipeName(recipe.getTitle())
                                .consumed(isNowConsumed)
                                .consumedAt(mealSlot.getConsumedAt())
                                .message(isNowConsumed ? "Đã đánh dấu bữa ăn đã được thực hiện"
                                                : "Đã hủy đánh dấu bữa ăn")
                                .build();
        }

        // ==================== Food Log Methods ====================

        @Override
        @Transactional
        public FoodLogResponse logFood(Long userId, LogFoodRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Recipe recipe = recipeRepository.findById(request.getRecipeId())
                                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

                FoodLog foodLog = FoodLog.builder()
                                .user(user)
                                .recipe(recipe)
                                .consumeDate(request.getConsumeDate())
                                .quantity(request.getQuantity() != null ? request.getQuantity() : BigDecimal.ONE)
                                .build();

                FoodLog saved = foodLogRepository.save(foodLog);

                // Invalidate cached nutrition log for this date
                LocalDate date = request.getConsumeDate().toLocalDate();
                dailyNutritionLogRepository.findByUser_UserIdAndDate(userId, date)
                                .ifPresent(dailyNutritionLogRepository::delete);

                log.info("User {} logged food: recipe={}, date={}, quantity={}",
                                userId, recipe.getTitle(), request.getConsumeDate(), saved.getQuantity());

                return mapToFoodLogResponse(saved);
        }

        @Override
        public DailyFoodLogsResponse getFoodLogs(Long userId, LocalDate date) {
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

                List<FoodLog> foodLogs = foodLogRepository.findByUser_UserIdAndConsumeDateBetweenOrderByConsumeDateAsc(
                                userId,
                                startOfDay, endOfDay);

                List<FoodLogResponse> responses = foodLogs.stream()
                                .map(this::mapToFoodLogResponse)
                                .toList();

                BigDecimal totalCalories = responses.stream()
                                .map(FoodLogResponse::getCalories)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalProtein = responses.stream()
                                .map(FoodLogResponse::getProtein)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalCarbs = responses.stream()
                                .map(FoodLogResponse::getCarbs)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalFat = responses.stream()
                                .map(FoodLogResponse::getFat)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return DailyFoodLogsResponse.builder()
                                .date(date)
                                .foodLogs(responses)
                                .totalEntries(responses.size())
                                .totalCalories(totalCalories)
                                .totalProtein(totalProtein)
                                .totalCarbs(totalCarbs)
                                .totalFat(totalFat)
                                .build();
        }

        @Override
        @Transactional
        public void deleteFoodLog(Long userId, Long foodLogId) {
                if (!foodLogRepository.existsByIdAndUser_UserId(foodLogId, userId)) {
                        throw new AppException(ErrorCode.NOT_FOUND, "Food log not found or access denied");
                }

                FoodLog foodLog = foodLogRepository.findById(foodLogId).orElse(null);
                LocalDate logDate = foodLog != null ? foodLog.getConsumeDate().toLocalDate() : null;

                foodLogRepository.deleteById(foodLogId);

                // Invalidate cached nutrition log for this date
                if (logDate != null) {
                        dailyNutritionLogRepository.findByUser_UserIdAndDate(userId, logDate)
                                        .ifPresent(dailyNutritionLogRepository::delete);
                }

                log.info("User {} deleted food log {}", userId, foodLogId);
        }

        private FoodLogResponse mapToFoodLogResponse(FoodLog foodLog) {
                Recipe recipe = foodLog.getRecipe();
                BigDecimal quantity = foodLog.getQuantity() != null ? foodLog.getQuantity() : BigDecimal.ONE;

                // Calculate nutrition based on quantity
                Map<String, BigDecimal> nutrition = calculateRecipeNutrition(recipe);
                BigDecimal calories = recipe.getCalories() != null
                                ? recipe.getCalories().multiply(quantity)
                                : BigDecimal.ZERO;

                return FoodLogResponse.builder()
                                .id(foodLog.getId())
                                .recipeId(recipe.getRecipeId())
                                .recipeName(recipe.getTitle())
                                .recipeImageUrl(recipe.getImageUrl())
                                .consumeDate(foodLog.getConsumeDate())
                                .quantity(quantity)
                                .calories(calories)
                                .protein(nutrition.getOrDefault("protein", BigDecimal.ZERO).multiply(quantity))
                                .carbs(nutrition.getOrDefault("carbs", BigDecimal.ZERO).multiply(quantity))
                                .fat(nutrition.getOrDefault("fat", BigDecimal.ZERO).multiply(quantity))
                                .build();
        }
}
