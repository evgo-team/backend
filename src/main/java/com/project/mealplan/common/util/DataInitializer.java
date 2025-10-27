package com.project.mealplan.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.mealplan.common.enums.IngredientType;
import com.project.mealplan.common.enums.MealRole;
import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.enums.NutritionUnit;
import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.common.enums.UserStatus;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.IngredientNutrition;
import com.project.mealplan.entity.NutritionType;
import com.project.mealplan.entity.Recipe;
import com.project.mealplan.entity.RecipeCategory;
import com.project.mealplan.entity.RecipeIngredient;
import com.project.mealplan.entity.Role;
import com.project.mealplan.entity.User;
import com.project.mealplan.repository.IngredientNutritionRepository;
import com.project.mealplan.repository.IngredientRepository;
import com.project.mealplan.repository.NutritionTypeRepository;
import com.project.mealplan.repository.RecipeCategoryRepository;
import com.project.mealplan.repository.RecipeRepository;
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
    private final RecipeRepository recipeRepository;
    private final RecipeCategoryRepository recipeCategoryRepository;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            initRoles();
            initUsers();
            initNutritionTypes();
            initIngredients();
            initCategories();
            initRecipes();
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
                new NutritionType("Calories", NutritionUnit.CAL),
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

    /** ======================= RECIPE CATEGORIES ======================= */
    private void initCategories() {
        if (recipeCategoryRepository.count() > 0) {
                System.out.println("Recipe categories already exist — skip initialization.");
                return;
        }

        System.out.println("📚 Initializing recipe categories...");

        List<RecipeCategory> cats = List.of(
                new RecipeCategory(null, "Asian"),
                new RecipeCategory(null, "Vegetarian"),
                new RecipeCategory(null, "Healthy"),
                new RecipeCategory(null, "Dessert")
        );

        recipeCategoryRepository.saveAll(cats);
        System.out.println("Recipe categories initialized.");
    }


    /** ======================= RECIPE ======================= */
    private void initRecipes() {
        if (recipeRepository.count() > 0) {
            System.out.println("Recipes already exist — skip initialization.");
            return;
        }

        System.out.println("🍳 Initializing recipe data...");

        // Lấy nguyên liệu
        Ingredient chicken = ingredientRepository.findByNameIgnoreCase("Chicken Breast")
                .orElseThrow(() -> new IllegalStateException("Chicken not found"));
        Ingredient rice = ingredientRepository.findByNameIgnoreCase("Rice")
                .orElseThrow(() -> new IllegalStateException("Rice not found"));
        Ingredient broccoli = ingredientRepository.findByNameIgnoreCase("Broccoli")
                .orElseThrow(() -> new IllegalStateException("Broccoli not found"));

        // Lấy hoặc tạo category
        RecipeCategory asianCat = recipeCategoryRepository.findByNameIgnoreCase("Asian")
                .orElseGet(() -> recipeCategoryRepository.save(new RecipeCategory(null, "Asian")));
        RecipeCategory vegCat = recipeCategoryRepository.findByNameIgnoreCase("Vegetarian")
                .orElseGet(() -> recipeCategoryRepository.save(new RecipeCategory(null, "Vegetarian")));
        RecipeCategory healthyCat = recipeCategoryRepository.findByNameIgnoreCase("Healthy")
                .orElseGet(() -> recipeCategoryRepository.save(new RecipeCategory(null, "Healthy")));

        /* ----------------- Recipe 1: Chicken Rice Bowl ----------------- */
        Recipe chickenRice = new Recipe();
        chickenRice.setTitle("Chicken Rice Bowl");
        chickenRice.setDescription("A healthy and balanced meal with chicken, rice, and broccoli.");
        chickenRice.setInstructions("""
            1. Cook rice.
            2. Grill chicken.
            3. Steam broccoli.
            4. Combine all and serve.
        """);
        chickenRice.setCookingTimeMinutes(30);
        chickenRice.setImageUrl("https://example.com/chicken_rice_bowl.jpg");
        chickenRice.setStatus(RecipeStatus.PUBLISHED);
        chickenRice.setRole(MealRole.MAIN_DISH);
        chickenRice.setMealType(MealType.LUNCH);
        chickenRice.getCategories().add(asianCat);
        chickenRice.getCategories().add(healthyCat);

        chickenRice.addIngredient(new RecipeIngredient(null, chickenRice, chicken, 150.0, "g"));
        chickenRice.addIngredient(new RecipeIngredient(null, chickenRice, rice, 200.0, "g"));
        chickenRice.addIngredient(new RecipeIngredient(null, chickenRice, broccoli, 100.0, "g"));

        /* ----------------- Recipe 2: Broccoli Stir-Fry ----------------- */
        Recipe broccoliStirFry = new Recipe();
        broccoliStirFry.setTitle("Broccoli Stir-Fry");
        broccoliStirFry.setDescription("Simple vegetarian stir-fry with broccoli and rice.");
        broccoliStirFry.setInstructions("""
            1. Stir-fry broccoli with garlic.
            2. Add soy sauce.
            3. Serve with rice.
        """);
        broccoliStirFry.setCookingTimeMinutes(20);
        broccoliStirFry.setImageUrl("https://example.com/broccoli_stir_fry.jpg");
        broccoliStirFry.setStatus(RecipeStatus.PUBLISHED);
        broccoliStirFry.setRole(MealRole.SIDE_DISH);
        broccoliStirFry.setMealType(MealType.DINNER);
        broccoliStirFry.getCategories().add(asianCat);
        broccoliStirFry.getCategories().add(vegCat);

        broccoliStirFry.addIngredient(new RecipeIngredient(null, broccoliStirFry, broccoli, 200.0, "g"));
        broccoliStirFry.addIngredient(new RecipeIngredient(null, broccoliStirFry, rice, 150.0, "g"));

        /* ----------------- Recipe 3: Simple Chicken Salad ----------------- */
        Recipe chickenSalad = new Recipe();
        chickenSalad.setTitle("Simple Chicken Salad");
        chickenSalad.setDescription("Light salad with grilled chicken and greens.");
        chickenSalad.setInstructions("""
            1. Grill chicken and slice.
            2. Toss with mixed greens and dressing.
            3. Serve chilled.
        """);
        chickenSalad.setCookingTimeMinutes(15);
        chickenSalad.setImageUrl("https://example.com/chicken_salad.jpg");
        chickenSalad.setStatus(RecipeStatus.DRAFT);
        chickenSalad.setRole(MealRole.MAIN_DISH);
        chickenSalad.setMealType(MealType.LUNCH);
        chickenSalad.getCategories().add(healthyCat);

        chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, chicken, 120.0, "g"));
        chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, broccoli, 50.0, "g"));

        /* ----------------- Save all ----------------- */
        recipeRepository.saveAll(List.of(chickenRice, broccoliStirFry, chickenSalad));

        System.out.println("✅ Recipe data initialized successfully!");
    }

}
