package com.project.mealplan.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.mealplan.common.enums.IngredientType;
import com.project.mealplan.common.enums.NutritionUnit;
import com.project.mealplan.common.enums.UserStatus;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.IngredientNutrition;
import com.project.mealplan.entity.NutritionType;
import com.project.mealplan.entity.Role;
import com.project.mealplan.entity.User;
import com.project.mealplan.repository.IngredientNutritionRepository;
import com.project.mealplan.repository.IngredientRepository;
import com.project.mealplan.repository.NutritionTypeRepository;
import com.project.mealplan.repository.RoleRepository;
import com.project.mealplan.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final IngredientRepository ingredientRepository;
    private final NutritionTypeRepository nutritionTypeRepository;
    private final IngredientNutritionRepository ingredientNutritionRepository;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            initRoles();
            initUsers();
            initNutritionTypes();
            initIngredients();
        };
    }

    /** ======================= ROLE ======================= */
    private void initRoles() {
        if (roleRepository.count() > 0) {
            System.out.println("Roles already exist — skip initialization.");
            return;
        }

        roleRepository.saveAll(List.of(
                new Role("ADMIN"),
                new Role("USER")
        ));

        System.out.println("✅ Roles initialized.");
    }

    /** ======================= USER ======================= */
    private void initUsers() {
        if (userRepository.count() > 0) {
            System.out.println("Users already exist — skip initialization.");
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Admin role not found"));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("User role not found"));

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setFullName("Admin User");
        admin.setStatus(UserStatus.ACTIVE);
        admin.setRoles(Set.of(adminRole));

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setFullName("Normal User");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(userRole));

        userRepository.saveAll(List.of(admin, user));

        System.out.println("Default users initialized: admin@example.com / user@example.com");
    }

    /** ======================= NUTRITION TYPE ======================= */
    private void initNutritionTypes() {
        if (nutritionTypeRepository.count() > 0) {
            System.out.println("Nutrition types already exist — skip initialization.");
            return;
        }

        nutritionTypeRepository.saveAll(List.of(
                new NutritionType("Calories", NutritionUnit.KCAL),
                new NutritionType("Protein", NutritionUnit.G),
                new NutritionType("Carbohydrate", NutritionUnit.G),
                new NutritionType("Fat", NutritionUnit.G),
                new NutritionType("Fiber", NutritionUnit.G),
                new NutritionType("Sugar", NutritionUnit.G),
                new NutritionType("Sodium", NutritionUnit.MG)
        ));

        System.out.println("Nutrition types initialized.");
    }

    /** ======================= INGREDIENT ======================= */
    private void initIngredients() {
        if (ingredientRepository.count() > 0) {
            System.out.println("Ingredients already exist — skip initialization.");
            return;
        }

        System.out.println("🌱 Initializing ingredient data...");

        // Lấy các NutritionType đã có từ DB
        NutritionType calories = nutritionTypeRepository.findByNameIgnoreCase("Calories")
                .orElseThrow(() -> new IllegalStateException("Calories not found"));
        NutritionType protein = nutritionTypeRepository.findByNameIgnoreCase("Protein")
                .orElseThrow(() -> new IllegalStateException("Protein not found"));
        NutritionType carbs = nutritionTypeRepository.findByNameIgnoreCase("Carbohydrate")
                .orElseThrow(() -> new IllegalStateException("Carbohydrate not found"));
        NutritionType fat = nutritionTypeRepository.findByNameIgnoreCase("Fat")
                .orElseThrow(() -> new IllegalStateException("Fat not found"));

        // ===== Nguyên liệu mẫu =====
        Ingredient chicken = new Ingredient();
        chicken.setName("Chicken Breast");
        chicken.setType(IngredientType.MEAT);

        Ingredient rice = new Ingredient();
        rice.setName("Rice");
        rice.setType(IngredientType.GRAIN);

        Ingredient broccoli = new Ingredient();
        broccoli.setName("Broccoli");
        broccoli.setType(IngredientType.VEGETABLE);

        ingredientRepository.saveAll(List.of(chicken, rice, broccoli));

        // ===== Nutrition cho từng nguyên liệu =====
        IngredientNutrition chickenCal = new IngredientNutrition(null, chicken, calories, new BigDecimal("165.0"));
        IngredientNutrition chickenPro = new IngredientNutrition(null, chicken, protein, new BigDecimal("31.0"));
        IngredientNutrition chickenFat = new IngredientNutrition(null, chicken, fat, new BigDecimal("3.6"));

        IngredientNutrition riceCal = new IngredientNutrition(null, rice, calories, new BigDecimal("130.0"));
        IngredientNutrition riceCarb = new IngredientNutrition(null, rice, carbs, new BigDecimal("28.0"));
        IngredientNutrition ricePro = new IngredientNutrition(null, rice, protein, new BigDecimal("2.7"));

        IngredientNutrition brocCal = new IngredientNutrition(null, broccoli, calories, new BigDecimal("35.0"));
        IngredientNutrition brocCarb = new IngredientNutrition(null, broccoli, carbs, new BigDecimal("7.0"));
        IngredientNutrition brocPro = new IngredientNutrition(null, broccoli, protein, new BigDecimal("2.5"));

        ingredientNutritionRepository.saveAll(List.of(
                chickenCal, chickenPro, chickenFat,
                riceCal, riceCarb, ricePro,
                brocCal, brocCarb, brocPro
        ));

        System.out.println("Ingredient data initialized successfully!");
    }
}

