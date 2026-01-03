package com.project.mealplan.common.util;

import com.project.mealplan.common.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                                new Role("USER")));

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

                user.setAge(25);
                user.setWeight(new BigDecimal("60"));
                user.setHeight(new BigDecimal("165"));
                user.setGender(Gender.FEMALE);
                user.setActivityLevel(ActivityLevel.MODERATE);

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
                                new NutritionType("Sodium", NutritionUnit.MG)));

                System.out.println("Nutrition types initialized.");
        }

        /** ======================= INGREDIENT ======================= */
        private void initIngredients() {
                if (ingredientRepository.count() > 0) {
                        System.out.println("Ingredients already exist — skip initialization.");
                        return;
                }

                System.out.println("🌱 Initializing ingredient data ...");

                // Lấy các NutritionType đã có từ DB
                NutritionType calories = nutritionTypeRepository.findByNameIgnoreCase("Calories")
                                .orElseThrow(() -> new IllegalStateException("Calories not found"));
                NutritionType protein = nutritionTypeRepository.findByNameIgnoreCase("Protein")
                                .orElseThrow(() -> new IllegalStateException("Protein not found"));
                NutritionType carbs = nutritionTypeRepository.findByNameIgnoreCase("Carbohydrate")
                                .orElseThrow(() -> new IllegalStateException("Carbohydrate not found"));
                NutritionType fat = nutritionTypeRepository.findByNameIgnoreCase("Fat")
                                .orElseThrow(() -> new IllegalStateException("Fat not found"));

                Ingredient chicken = new Ingredient();
                chicken.setName("Chicken Breast");
                chicken.setType(IngredientType.MEAT);

                Ingredient rice = new Ingredient();
                rice.setName("Rice");
                rice.setType(IngredientType.GRAIN);

                Ingredient broccoli = new Ingredient();
                broccoli.setName("Broccoli");
                broccoli.setType(IngredientType.VEGETABLE);

                Ingredient carrot = new Ingredient();
                carrot.setName("Carrot");
                carrot.setType(IngredientType.VEGETABLE);

                Ingredient groundBeef = new Ingredient();
                groundBeef.setName("Ground Beef");
                groundBeef.setType(IngredientType.MEAT);

                Ingredient tomato = new Ingredient();
                tomato.setName("Tomato");
                tomato.setType(IngredientType.VEGETABLE);

                Ingredient oliveOil = new Ingredient();
                oliveOil.setName("Olive Oil");
                oliveOil.setType(IngredientType.OIL_AND_FAT);

                Ingredient onion = new Ingredient();
                onion.setName("Onion");
                onion.setType(IngredientType.VEGETABLE);

                Ingredient garlic = new Ingredient();
                garlic.setName("Garlic");
                garlic.setType(IngredientType.VEGETABLE);

                Ingredient potato = new Ingredient();
                potato.setName("Potato");
                potato.setType(IngredientType.VEGETABLE);

                Ingredient salmon = new Ingredient();
                salmon.setName("Salmon");
                salmon.setType(IngredientType.SEAFOOD);

                Ingredient egg = new Ingredient();
                egg.setName("Egg");
                egg.setType(IngredientType.EGG);

                Ingredient milk = new Ingredient();
                milk.setName("Milk");
                milk.setType(IngredientType.DAIRY);

                Ingredient flour = new Ingredient();
                flour.setName("All-Purpose Flour");
                flour.setType(IngredientType.GRAIN);

                Ingredient sugar = new Ingredient();
                sugar.setName("Sugar");
                sugar.setType(IngredientType.SEASONING);

                Ingredient salt = new Ingredient();
                salt.setName("Salt");
                salt.setType(IngredientType.SEASONING);

                Ingredient lemon = new Ingredient();
                lemon.setName("Lemon");
                lemon.setType(IngredientType.FRUIT);

                Ingredient lettuce = new Ingredient();
                lettuce.setName("Lettuce");
                lettuce.setType(IngredientType.VEGETABLE);

                Ingredient cheese = new Ingredient();
                cheese.setName("Cheddar Cheese");
                cheese.setType(IngredientType.DAIRY);

                Ingredient butter = new Ingredient();
                butter.setName("Butter");
                butter.setType(IngredientType.OIL_AND_FAT);

                Ingredient beefShank = new Ingredient();
                beefShank.setName("Beef Shank"); // Bắp bò (cho Phở)
                beefShank.setType(IngredientType.MEAT);

                Ingredient riceNoodles = new Ingredient();
                riceNoodles.setName("Rice Noodles"); // Bánh phở
                riceNoodles.setType(IngredientType.GRAIN);

                Ingredient ginger = new Ingredient();
                ginger.setName("Ginger"); // Gừng
                ginger.setType(IngredientType.VEGETABLE);

                Ingredient greenOnion = new Ingredient();
                greenOnion.setName("Green Onion"); // Hành lá
                greenOnion.setType(IngredientType.VEGETABLE);

                Ingredient starAnise = new Ingredient();
                starAnise.setName("Star Anise"); // Hoa hồi
                starAnise.setType(IngredientType.SEASONING);

                Ingredient cinnamonStick = new Ingredient();
                cinnamonStick.setName("Cinnamon Stick"); // Quế
                cinnamonStick.setType(IngredientType.SEASONING);

                Ingredient fishSauce = new Ingredient();
                fishSauce.setName("Fish Sauce"); // Nước mắm
                fishSauce.setType(IngredientType.SEASONING);

                Ingredient lemongrass = new Ingredient();
                lemongrass.setName("Lemongrass"); // Sả
                lemongrass.setType(IngredientType.VEGETABLE);

                Ingredient chili = new Ingredient();
                chili.setName("Chili"); // Ớt
                chili.setType(IngredientType.VEGETABLE);

                Ingredient waterSpinach = new Ingredient();
                waterSpinach.setName("Water Spinach"); // Rau muống
                waterSpinach.setType(IngredientType.VEGETABLE);

                Ingredient tofu = new Ingredient();
                tofu.setName("Tofu"); // Đậu hũ
                tofu.setType(IngredientType.LEGUME);

                Ingredient groundPork = new Ingredient();
                groundPork.setName("Ground Pork"); // Thịt heo xay
                groundPork.setType(IngredientType.MEAT);

                Ingredient ricePaper = new Ingredient();
                ricePaper.setName("Rice Paper"); // Bánh tráng
                ricePaper.setType(IngredientType.GRAIN);

                Ingredient woodEarMushroom = new Ingredient();
                woodEarMushroom.setName("Wood Ear Mushroom"); // Nấm mèo
                woodEarMushroom.setType(IngredientType.VEGETABLE);

                Ingredient beanSprouts = new Ingredient();
                beanSprouts.setName("Bean Sprouts"); // Giá đỗ
                beanSprouts.setType(IngredientType.VEGETABLE);

                Ingredient cilantro = new Ingredient();
                cilantro.setName("Cilantro"); // Ngò rí
                cilantro.setType(IngredientType.VEGETABLE);

                Ingredient vegetableOil = new Ingredient();
                vegetableOil.setName("Vegetable Oil"); // Dầu thực vật
                vegetableOil.setType(IngredientType.OIL_AND_FAT);

                Ingredient soySauce = new Ingredient();
                soySauce.setName("Soy Sauce"); // Nước tương
                soySauce.setType(IngredientType.SEASONING);

                Ingredient porkBelly = new Ingredient();
                porkBelly.setName("Pork Belly"); // Thịt ba rọi
                porkBelly.setType(IngredientType.MEAT);

                Ingredient coconutWater = new Ingredient();
                coconutWater.setName("Coconut Water"); // Nước dừa
                coconutWater.setType(IngredientType.BEVERAGE);

                Ingredient oysterSauce = new Ingredient();
                oysterSauce.setName("Oyster Sauce"); // Dầu hào
                oysterSauce.setType(IngredientType.SEASONING);

                Ingredient beefSirloin = new Ingredient();
                beefSirloin.setName("Beef Sirloin"); // Bò thăn
                beefSirloin.setType(IngredientType.MEAT);

                Ingredient catfish = new Ingredient();
                catfish.setName("Catfish"); // Cá lóc/basa
                catfish.setType(IngredientType.SEAFOOD);

                Ingredient tamarindPaste = new Ingredient();
                tamarindPaste.setName("Tamarind Paste"); // Me vắt
                tamarindPaste.setType(IngredientType.SEASONING);

                Ingredient okra = new Ingredient();
                okra.setName("Okra"); // Đậu bắp
                okra.setType(IngredientType.VEGETABLE);

                Ingredient shrimp = new Ingredient();
                shrimp.setName("Shrimp"); // Tôm
                shrimp.setType(IngredientType.SEAFOOD);

                Ingredient peanuts = new Ingredient();
                peanuts.setName("Peanuts"); // Đậu phộng
                peanuts.setType(IngredientType.NUT_AND_SEED);

                Ingredient peas = new Ingredient();
                peas.setName("Peas"); // Đậu Hà Lan
                peas.setType(IngredientType.VEGETABLE);

                Ingredient thickRiceNoodles = new Ingredient();
                thickRiceNoodles.setName("Thick Rice Noodles"); // Bún bò
                thickRiceNoodles.setType(IngredientType.GRAIN);

                Ingredient porkHock = new Ingredient();
                porkHock.setName("Pork Hock"); // Giò heo
                porkHock.setType(IngredientType.MEAT);

                Ingredient shrimpPaste = new Ingredient();
                shrimpPaste.setName("Shrimp Paste"); // Mắm ruốc
                shrimpPaste.setType(IngredientType.SEASONING);

                Ingredient porkChop = new Ingredient();
                porkChop.setName("Pork Chop"); // Sườn cốt lết
                porkChop.setType(IngredientType.MEAT);

                Ingredient brokenRice = new Ingredient();
                brokenRice.setName("Broken Rice"); // Cơm tấm
                brokenRice.setType(IngredientType.GRAIN);

                Ingredient honey = new Ingredient();
                honey.setName("Honey"); // Mật ong
                honey.setType(IngredientType.SEASONING);

                Ingredient sweetCorn = new Ingredient();
                sweetCorn.setName("Sweet Corn"); // Ngô ngọt
                sweetCorn.setType(IngredientType.VEGETABLE);

                Ingredient cornstarch = new Ingredient();
                cornstarch.setName("Cornstarch"); // Bột bắp
                cornstarch.setType(IngredientType.GRAIN);

                Ingredient condensedMilk = new Ingredient();
                condensedMilk.setName("Condensed Milk"); // Sữa đặc
                condensedMilk.setType(IngredientType.DAIRY);

                Ingredient vanillaExtract = new Ingredient();
                vanillaExtract.setName("Vanilla Extract"); // Chiết xuất vani
                vanillaExtract.setType(IngredientType.SEASONING);

                Ingredient chiliOil = new Ingredient();
                chiliOil.setName("Chili Oil"); // Sa tế / Dầu ớt
                chiliOil.setType(IngredientType.SEASONING);

                oliveOil.setDensity(BigDecimal.valueOf(0.91)); // g/ml
                vegetableOil.setDensity(BigDecimal.valueOf(0.92));
                butter.setDensity(BigDecimal.valueOf(0.96)); // melted butter

                fishSauce.setDensity(BigDecimal.valueOf(1.10));
                soySauce.setDensity(BigDecimal.valueOf(1.10));
                oysterSauce.setDensity(BigDecimal.valueOf(1.24));
                shrimpPaste.setDensity(BigDecimal.valueOf(1.30));

                coconutWater.setDensity(BigDecimal.valueOf(1.03));
                milk.setDensity(BigDecimal.valueOf(1.03));
                condensedMilk.setDensity(BigDecimal.valueOf(1.31));

                honey.setDensity(BigDecimal.valueOf(1.42));
                tamarindPaste.setDensity(BigDecimal.valueOf(1.10));
                chiliOil.setDensity(BigDecimal.valueOf(0.92));

                vanillaExtract.setDensity(BigDecimal.valueOf(0.90));

                // ===== Lưu tất cả nguyên liệu =====
                ingredientRepository.saveAll(List.of(
                                chicken, rice, broccoli, carrot, groundBeef, tomato, oliveOil, onion,
                                garlic, potato, salmon, egg, milk, flour, sugar, salt, lemon, lettuce, cheese, butter,
                                beefShank, riceNoodles, ginger, greenOnion, starAnise, cinnamonStick, fishSauce,
                                lemongrass, chili, waterSpinach, tofu, groundPork, ricePaper, woodEarMushroom,
                                beanSprouts, cilantro, vegetableOil, soySauce, porkBelly, coconutWater, oysterSauce,
                                beefSirloin, catfish, tamarindPaste, okra, shrimp, peanuts, peas,
                                thickRiceNoodles, porkHock, shrimpPaste, porkChop, brokenRice,
                                honey, sweetCorn, cornstarch, condensedMilk, vanillaExtract, chiliOil));

                // ===== Nutrition cho nguyên liệu =====
                IngredientNutrition chickenCal = new IngredientNutrition(null, chicken, calories,
                                new BigDecimal("165.0"));
                IngredientNutrition chickenPro = new IngredientNutrition(null, chicken, protein,
                                new BigDecimal("31.0"));
                IngredientNutrition chickenFat = new IngredientNutrition(null, chicken, fat, new BigDecimal("3.6"));

                IngredientNutrition riceCal = new IngredientNutrition(null, rice, calories, new BigDecimal("130.0"));
                IngredientNutrition riceCarb = new IngredientNutrition(null, rice, carbs, new BigDecimal("28.0"));
                IngredientNutrition ricePro = new IngredientNutrition(null, rice, protein, new BigDecimal("2.7"));

                IngredientNutrition brocCal = new IngredientNutrition(null, broccoli, calories, new BigDecimal("35.0"));
                IngredientNutrition brocCarb = new IngredientNutrition(null, broccoli, carbs, new BigDecimal("7.0"));
                IngredientNutrition brocPro = new IngredientNutrition(null, broccoli, protein, new BigDecimal("2.5"));

                IngredientNutrition carrotCal = new IngredientNutrition(null, carrot, calories, new BigDecimal("41.0"));
                IngredientNutrition carrotCarb = new IngredientNutrition(null, carrot, carbs, new BigDecimal("9.6"));

                IngredientNutrition beefCal = new IngredientNutrition(null, groundBeef, calories,
                                new BigDecimal("250.0"));
                IngredientNutrition beefPro = new IngredientNutrition(null, groundBeef, protein,
                                new BigDecimal("26.0"));
                IngredientNutrition beefFat = new IngredientNutrition(null, groundBeef, fat, new BigDecimal("15.0"));

                IngredientNutrition tomatoCal = new IngredientNutrition(null, tomato, calories, new BigDecimal("18.0"));
                IngredientNutrition tomatoCarb = new IngredientNutrition(null, tomato, carbs, new BigDecimal("3.9"));

                IngredientNutrition oilCal = new IngredientNutrition(null, oliveOil, calories, new BigDecimal("884.0"));
                IngredientNutrition oilFat = new IngredientNutrition(null, oliveOil, fat, new BigDecimal("100.0"));

                IngredientNutrition onionCal = new IngredientNutrition(null, onion, calories, new BigDecimal("40.0"));
                IngredientNutrition onionCarb = new IngredientNutrition(null, onion, carbs, new BigDecimal("9.3"));

                // ===== Nutrition cho 12 nguyên liệu mới =====
                IngredientNutrition garlicCal = new IngredientNutrition(null, garlic, calories,
                                new BigDecimal("149.0"));
                IngredientNutrition garlicCarb = new IngredientNutrition(null, garlic, carbs, new BigDecimal("33.0"));

                IngredientNutrition potatoCal = new IngredientNutrition(null, potato, calories, new BigDecimal("77.0"));
                IngredientNutrition potatoCarb = new IngredientNutrition(null, potato, carbs, new BigDecimal("17.0"));
                IngredientNutrition potatoPro = new IngredientNutrition(null, potato, protein, new BigDecimal("2.0"));

                IngredientNutrition salmonCal = new IngredientNutrition(null, salmon, calories,
                                new BigDecimal("208.0"));
                IngredientNutrition salmonPro = new IngredientNutrition(null, salmon, protein, new BigDecimal("20.0"));
                IngredientNutrition salmonFat = new IngredientNutrition(null, salmon, fat, new BigDecimal("13.0"));

                IngredientNutrition eggCal = new IngredientNutrition(null, egg, calories, new BigDecimal("155.0"));
                IngredientNutrition eggPro = new IngredientNutrition(null, egg, protein, new BigDecimal("13.0"));
                IngredientNutrition eggFat = new IngredientNutrition(null, egg, fat, new BigDecimal("11.0"));

                IngredientNutrition milkCal = new IngredientNutrition(null, milk, calories, new BigDecimal("42.0"));
                IngredientNutrition milkPro = new IngredientNutrition(null, milk, protein, new BigDecimal("3.4"));
                IngredientNutrition milkCarb = new IngredientNutrition(null, milk, carbs, new BigDecimal("5.0"));
                IngredientNutrition milkFat = new IngredientNutrition(null, milk, fat, new BigDecimal("1.0"));

                IngredientNutrition flourCal = new IngredientNutrition(null, flour, calories, new BigDecimal("364.0"));
                IngredientNutrition flourPro = new IngredientNutrition(null, flour, protein, new BigDecimal("10.0"));
                IngredientNutrition flourCarb = new IngredientNutrition(null, flour, carbs, new BigDecimal("76.0"));

                IngredientNutrition sugarCal = new IngredientNutrition(null, sugar, calories, new BigDecimal("387.0"));
                IngredientNutrition sugarCarb = new IngredientNutrition(null, sugar, carbs, new BigDecimal("100.0"));

                IngredientNutrition saltCal = new IngredientNutrition(null, salt, calories, new BigDecimal("0.0"));

                IngredientNutrition lemonCal = new IngredientNutrition(null, lemon, calories, new BigDecimal("29.0"));
                IngredientNutrition lemonCarb = new IngredientNutrition(null, lemon, carbs, new BigDecimal("9.0"));

                IngredientNutrition lettuceCal = new IngredientNutrition(null, lettuce, calories,
                                new BigDecimal("15.0"));
                IngredientNutrition lettuceCarb = new IngredientNutrition(null, lettuce, carbs, new BigDecimal("2.9"));

                IngredientNutrition cheeseCal = new IngredientNutrition(null, cheese, calories,
                                new BigDecimal("404.0"));
                IngredientNutrition cheesePro = new IngredientNutrition(null, cheese, protein, new BigDecimal("25.0"));
                IngredientNutrition cheeseFat = new IngredientNutrition(null, cheese, fat, new BigDecimal("33.0"));

                IngredientNutrition butterCal = new IngredientNutrition(null, butter, calories,
                                new BigDecimal("717.0"));
                IngredientNutrition butterFat = new IngredientNutrition(null, butter, fat, new BigDecimal("81.0"));

                IngredientNutrition beefShankCal = new IngredientNutrition(null, beefShank, calories,
                                new BigDecimal("190.0"));
                IngredientNutrition beefShankPro = new IngredientNutrition(null, beefShank, protein,
                                new BigDecimal("28.0"));
                IngredientNutrition beefShankFat = new IngredientNutrition(null, beefShank, fat, new BigDecimal("8.0"));

                IngredientNutrition riceNoodlesCal = new IngredientNutrition(null, riceNoodles, calories,
                                new BigDecimal("130.0"));
                IngredientNutrition riceNoodlesCarb = new IngredientNutrition(null, riceNoodles, carbs,
                                new BigDecimal("28.0"));
                IngredientNutrition riceNoodlesPro = new IngredientNutrition(null, riceNoodles, protein,
                                new BigDecimal("2.1"));

                IngredientNutrition gingerCal = new IngredientNutrition(null, ginger, calories, new BigDecimal("80.0"));
                IngredientNutrition gingerCarb = new IngredientNutrition(null, ginger, carbs, new BigDecimal("18.0"));

                IngredientNutrition greenOnionCal = new IngredientNutrition(null, greenOnion, calories,
                                new BigDecimal("32.0"));
                IngredientNutrition greenOnionCarb = new IngredientNutrition(null, greenOnion, carbs,
                                new BigDecimal("7.0"));

                IngredientNutrition starAniseCal = new IngredientNutrition(null, starAnise, calories,
                                new BigDecimal("337.0")); // Dùng rất ít
                IngredientNutrition cinnamonCal = new IngredientNutrition(null, cinnamonStick, calories,
                                new BigDecimal("247.0")); // Dùng rất ít

                IngredientNutrition fishSauceCal = new IngredientNutrition(null, fishSauce, calories,
                                new BigDecimal("40.0"));
                IngredientNutrition fishSaucePro = new IngredientNutrition(null, fishSauce, protein,
                                new BigDecimal("10.0")); // Đại diện

                IngredientNutrition lemongrassCal = new IngredientNutrition(null, lemongrass, calories,
                                new BigDecimal("99.0"));
                IngredientNutrition lemongrassCarb = new IngredientNutrition(null, lemongrass, carbs,
                                new BigDecimal("25.0"));

                IngredientNutrition chiliCal = new IngredientNutrition(null, chili, calories, new BigDecimal("40.0"));
                IngredientNutrition chiliCarb = new IngredientNutrition(null, chili, carbs, new BigDecimal("9.0"));

                IngredientNutrition waterSpinachCal = new IngredientNutrition(null, waterSpinach, calories,
                                new BigDecimal("19.0"));
                IngredientNutrition waterSpinachCarb = new IngredientNutrition(null, waterSpinach, carbs,
                                new BigDecimal("3.0"));

                IngredientNutrition tofuCal = new IngredientNutrition(null, tofu, calories, new BigDecimal("76.0"));
                IngredientNutrition tofuPro = new IngredientNutrition(null, tofu, protein, new BigDecimal("8.0"));
                IngredientNutrition tofuFat = new IngredientNutrition(null, tofu, fat, new BigDecimal("5.0"));

                IngredientNutrition groundPorkCal = new IngredientNutrition(null, groundPork, calories,
                                new BigDecimal("297.0"));
                IngredientNutrition groundPorkPro = new IngredientNutrition(null, groundPork, protein,
                                new BigDecimal("26.0"));
                IngredientNutrition groundPorkFat = new IngredientNutrition(null, groundPork, fat,
                                new BigDecimal("21.0"));

                IngredientNutrition ricePaperCal = new IngredientNutrition(null, ricePaper, calories,
                                new BigDecimal("340.0")); // 100g (khô)
                IngredientNutrition ricePaperCarb = new IngredientNutrition(null, ricePaper, carbs,
                                new BigDecimal("82.0"));

                IngredientNutrition woodEarMushroomCal = new IngredientNutrition(null, woodEarMushroom, calories,
                                new BigDecimal("25.0")); // (tươi)
                IngredientNutrition woodEarMushroomCarb = new IngredientNutrition(null, woodEarMushroom, carbs,
                                new BigDecimal("7.0"));

                IngredientNutrition beanSproutsCal = new IngredientNutrition(null, beanSprouts, calories,
                                new BigDecimal("30.0"));
                IngredientNutrition beanSproutsCarb = new IngredientNutrition(null, beanSprouts, carbs,
                                new BigDecimal("6.0"));
                IngredientNutrition beanSproutsPro = new IngredientNutrition(null, beanSprouts, protein,
                                new BigDecimal("3.0"));

                IngredientNutrition cilantroCal = new IngredientNutrition(null, cilantro, calories,
                                new BigDecimal("23.0"));
                IngredientNutrition cilantroCarb = new IngredientNutrition(null, cilantro, carbs,
                                new BigDecimal("4.0"));

                IngredientNutrition vegetableOilCal = new IngredientNutrition(null, vegetableOil, calories,
                                new BigDecimal("884.0"));
                IngredientNutrition vegetableOilFat = new IngredientNutrition(null, vegetableOil, fat,
                                new BigDecimal("100.0"));

                IngredientNutrition soySauceCal = new IngredientNutrition(null, soySauce, calories,
                                new BigDecimal("53.0"));
                IngredientNutrition soySaucePro = new IngredientNutrition(null, soySauce, protein,
                                new BigDecimal("8.0"));

                IngredientNutrition porkBellyCal = new IngredientNutrition(null, porkBelly, calories,
                                new BigDecimal("517.0"));
                IngredientNutrition porkBellyPro = new IngredientNutrition(null, porkBelly, protein,
                                new BigDecimal("9.0"));
                IngredientNutrition porkBellyFat = new IngredientNutrition(null, porkBelly, fat,
                                new BigDecimal("53.0"));

                IngredientNutrition coconutWaterCal = new IngredientNutrition(null, coconutWater, calories,
                                new BigDecimal("19.0"));
                IngredientNutrition coconutWaterCarb = new IngredientNutrition(null, coconutWater, carbs,
                                new BigDecimal("3.7"));

                IngredientNutrition oysterSauceCal = new IngredientNutrition(null, oysterSauce, calories,
                                new BigDecimal("51.0"));
                IngredientNutrition oysterSauceCarb = new IngredientNutrition(null, oysterSauce, carbs,
                                new BigDecimal("12.0"));

                IngredientNutrition beefSirloinCal = new IngredientNutrition(null, beefSirloin, calories,
                                new BigDecimal("170.0"));
                IngredientNutrition beefSirloinPro = new IngredientNutrition(null, beefSirloin, protein,
                                new BigDecimal("25.0"));
                IngredientNutrition beefSirloinFat = new IngredientNutrition(null, beefSirloin, fat,
                                new BigDecimal("7.0"));

                IngredientNutrition catfishCal = new IngredientNutrition(null, catfish, calories,
                                new BigDecimal("105.0"));
                IngredientNutrition catfishPro = new IngredientNutrition(null, catfish, protein,
                                new BigDecimal("18.0"));
                IngredientNutrition catfishFat = new IngredientNutrition(null, catfish, fat, new BigDecimal("3.0"));

                IngredientNutrition tamarindCal = new IngredientNutrition(null, tamarindPaste, calories,
                                new BigDecimal("239.0"));
                IngredientNutrition tamarindCarb = new IngredientNutrition(null, tamarindPaste, carbs,
                                new BigDecimal("62.0"));

                IngredientNutrition okraCal = new IngredientNutrition(null, okra, calories, new BigDecimal("33.0"));
                IngredientNutrition okraCarb = new IngredientNutrition(null, okra, carbs, new BigDecimal("7.0"));

                IngredientNutrition shrimpCal = new IngredientNutrition(null, shrimp, calories, new BigDecimal("99.0"));
                IngredientNutrition shrimpPro = new IngredientNutrition(null, shrimp, protein, new BigDecimal("24.0"));
                IngredientNutrition shrimpFat = new IngredientNutrition(null, shrimp, fat, new BigDecimal("0.3"));

                IngredientNutrition peanutsCal = new IngredientNutrition(null, peanuts, calories,
                                new BigDecimal("567.0"));
                IngredientNutrition peanutsPro = new IngredientNutrition(null, peanuts, protein,
                                new BigDecimal("26.0"));
                IngredientNutrition peanutsFat = new IngredientNutrition(null, peanuts, fat, new BigDecimal("49.0"));

                IngredientNutrition peasCal = new IngredientNutrition(null, peas, calories, new BigDecimal("81.0"));
                IngredientNutrition peasPro = new IngredientNutrition(null, peas, protein, new BigDecimal("5.0"));
                IngredientNutrition peasCarb = new IngredientNutrition(null, peas, carbs, new BigDecimal("14.0"));

                IngredientNutrition thickNoodlesCal = new IngredientNutrition(null, thickRiceNoodles, calories,
                                new BigDecimal("130.0"));
                IngredientNutrition thickNoodlesCarb = new IngredientNutrition(null, thickRiceNoodles, carbs,
                                new BigDecimal("28.0"));

                IngredientNutrition porkHockCal = new IngredientNutrition(null, porkHock, calories,
                                new BigDecimal("230.0"));
                IngredientNutrition porkHockPro = new IngredientNutrition(null, porkHock, protein,
                                new BigDecimal("20.0"));
                IngredientNutrition porkHockFat = new IngredientNutrition(null, porkHock, fat, new BigDecimal("16.0"));

                IngredientNutrition shrimpPasteCal = new IngredientNutrition(null, shrimpPaste, calories,
                                new BigDecimal("98.0"));

                IngredientNutrition porkChopCal = new IngredientNutrition(null, porkChop, calories,
                                new BigDecimal("231.0"));
                IngredientNutrition porkChopPro = new IngredientNutrition(null, porkChop, protein,
                                new BigDecimal("26.0"));
                IngredientNutrition porkChopFat = new IngredientNutrition(null, porkChop, fat, new BigDecimal("13.0"));

                IngredientNutrition brokenRiceCal = new IngredientNutrition(null, brokenRice, calories,
                                new BigDecimal("130.0")); // Tương tự Gạo
                IngredientNutrition brokenRiceCarb = new IngredientNutrition(null, brokenRice, carbs,
                                new BigDecimal("28.0"));

                IngredientNutrition honeyCal = new IngredientNutrition(null, honey, calories, new BigDecimal("304.0"));
                IngredientNutrition honeyCarb = new IngredientNutrition(null, honey, carbs, new BigDecimal("82.0"));

                IngredientNutrition cornCal = new IngredientNutrition(null, sweetCorn, calories,
                                new BigDecimal("86.0"));
                IngredientNutrition cornPro = new IngredientNutrition(null, sweetCorn, protein, new BigDecimal("3.2"));
                IngredientNutrition cornCarb = new IngredientNutrition(null, sweetCorn, carbs, new BigDecimal("19.0"));

                IngredientNutrition cornstarchCal = new IngredientNutrition(null, cornstarch, calories,
                                new BigDecimal("381.0"));
                IngredientNutrition cornstarchCarb = new IngredientNutrition(null, cornstarch, carbs,
                                new BigDecimal("91.0"));

                IngredientNutrition condensedMilkCal = new IngredientNutrition(null, condensedMilk, calories,
                                new BigDecimal("321.0"));
                IngredientNutrition condensedMilkCarb = new IngredientNutrition(null, condensedMilk, carbs,
                                new BigDecimal("55.0"));
                IngredientNutrition condensedMilkFat = new IngredientNutrition(null, condensedMilk, fat,
                                new BigDecimal("8.0"));

                IngredientNutrition vanillaCal = new IngredientNutrition(null, vanillaExtract, calories,
                                new BigDecimal("288.0")); // Do cồn

                IngredientNutrition chiliOilCal = new IngredientNutrition(null, chiliOil, calories,
                                new BigDecimal("884.0")); // Giống dầu
                IngredientNutrition chiliOilFat = new IngredientNutrition(null, chiliOil, fat, new BigDecimal("100.0"));

                // ===== Lưu tất cả nutrition =====
                ingredientNutritionRepository.saveAll(List.of(
                                chickenCal, chickenPro, chickenFat, riceCal, riceCarb, ricePro, brocCal, brocCarb,
                                brocPro,
                                carrotCal, carrotCarb, beefCal, beefPro, beefFat, tomatoCal, tomatoCarb, oilCal, oilFat,
                                onionCal, onionCarb, garlicCal, garlicCarb, potatoCal, potatoCarb, potatoPro,
                                salmonCal, salmonPro, salmonFat, eggCal, eggPro, eggFat, milkCal, milkPro, milkCarb,
                                milkFat,
                                flourCal, flourPro, flourCarb, sugarCal, sugarCarb, saltCal, lemonCal, lemonCarb,
                                lettuceCal, lettuceCarb, cheeseCal, cheesePro, cheeseFat, butterCal, butterFat,
                                beefShankCal, beefShankPro, beefShankFat, riceNoodlesCal, riceNoodlesCarb,
                                riceNoodlesPro,
                                gingerCal, gingerCarb, greenOnionCal, greenOnionCarb, starAniseCal, cinnamonCal,
                                fishSauceCal, fishSaucePro, lemongrassCal, lemongrassCarb, chiliCal, chiliCarb,
                                waterSpinachCal, waterSpinachCarb, tofuCal, tofuPro, tofuFat, groundPorkCal,
                                groundPorkPro, groundPorkFat,
                                ricePaperCal, ricePaperCarb, woodEarMushroomCal, woodEarMushroomCarb, beanSproutsCal,
                                beanSproutsCarb, beanSproutsPro,
                                cilantroCal, cilantroCarb, vegetableOilCal, vegetableOilFat,
                                soySauceCal, soySaucePro, porkBellyCal, porkBellyPro, porkBellyFat,
                                coconutWaterCal, coconutWaterCarb, oysterSauceCal, oysterSauceCarb,
                                beefSirloinCal, beefSirloinPro, beefSirloinFat,
                                catfishCal, catfishPro, catfishFat, tamarindCal, tamarindCarb,
                                okraCal, okraCarb, shrimpCal, shrimpPro, shrimpFat,
                                peanutsCal, peanutsPro, peanutsFat, peasCal, peasPro, peasCarb,
                                thickNoodlesCal, thickNoodlesCarb,
                                porkHockCal, porkHockPro, porkHockFat,
                                shrimpPasteCal,
                                porkChopCal, porkChopPro, porkChopFat,
                                brokenRiceCal, brokenRiceCarb,
                                honeyCal, honeyCarb,
                                cornCal, cornPro, cornCarb,
                                cornstarchCal, cornstarchCarb,
                                condensedMilkCal, condensedMilkCarb, condensedMilkFat,
                                vanillaCal,
                                chiliOilCal, chiliOilFat));

                System.out.println("✅ Ingredient data initialized successfully!");
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
                                new RecipeCategory(null, "Dessert"),
                                new RecipeCategory(null, "Italian"),
                                new RecipeCategory(null, "Salad"),
                                new RecipeCategory(null, "Quick & Easy"),
                                new RecipeCategory(null, "Vietnamese"),
                                new RecipeCategory(null, "Appetizer"),
                                new RecipeCategory(null, "Soup"),
                                new RecipeCategory(null, "Seafood"),
                                new RecipeCategory(null, "High Protein"),
                                new RecipeCategory(null, "Noodles"),
                                new RecipeCategory(null, "Grilled"),
                                new RecipeCategory(null, "Fried"),
                                new RecipeCategory(null, "Spicy"));
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

                // Lấy admin
                User admin = userRepository.findByEmail("admin@example.com")
                                .orElseThrow(() -> new IllegalStateException("Admin user not found"));

                // Lấy nguyên liệu
                Ingredient chicken = ingredientRepository.findByNameIgnoreCase("Chicken Breast")
                                .orElseThrow(() -> new IllegalStateException("Chicken not found"));
                Ingredient rice = ingredientRepository.findByNameIgnoreCase("Rice")
                                .orElseThrow(() -> new IllegalStateException("Rice not found"));
                Ingredient broccoli = ingredientRepository.findByNameIgnoreCase("Broccoli")
                                .orElseThrow(() -> new IllegalStateException("Broccoli not found"));
                Ingredient carrot = ingredientRepository.findByNameIgnoreCase("Carrot")
                                .orElseThrow(() -> new IllegalStateException("Carrot not found"));
                Ingredient groundBeef = ingredientRepository.findByNameIgnoreCase("Ground Beef")
                                .orElseThrow(() -> new IllegalStateException("Ground Beef not found"));
                Ingredient tomato = ingredientRepository.findByNameIgnoreCase("Tomato")
                                .orElseThrow(() -> new IllegalStateException("Tomato not found"));
                Ingredient oliveOil = ingredientRepository.findByNameIgnoreCase("Olive Oil")
                                .orElseThrow(() -> new IllegalStateException("Olive Oil not found"));
                Ingredient onion = ingredientRepository.findByNameIgnoreCase("Onion")
                                .orElseThrow(() -> new IllegalStateException("Onion not found"));
                Ingredient garlic = ingredientRepository.findByNameIgnoreCase("Garlic")
                                .orElseThrow(() -> new IllegalStateException("Garlic not found"));
                // Ingredient potato = ingredientRepository.findByNameIgnoreCase("Potato")
                // .orElseThrow(() -> new IllegalStateException("Potato not found"));
                // Ingredient salmon = ingredientRepository.findByNameIgnoreCase("Salmon")
                // .orElseThrow(() -> new IllegalStateException("Salmon not found"));
                Ingredient egg = ingredientRepository.findByNameIgnoreCase("Egg")
                                .orElseThrow(() -> new IllegalStateException("Egg not found"));
                Ingredient milk = ingredientRepository.findByNameIgnoreCase("Milk")
                                .orElseThrow(() -> new IllegalStateException("Milk not found"));
                // Ingredient flour = ingredientRepository.findByNameIgnoreCase("All-Purpose
                // Flour")
                // .orElseThrow(() -> new IllegalStateException("All-Purpose Flour not found"));
                Ingredient sugar = ingredientRepository.findByNameIgnoreCase("Sugar")
                                .orElseThrow(() -> new IllegalStateException("Sugar not found"));
                // Ingredient salt = ingredientRepository.findByNameIgnoreCase("Salt")
                // .orElseThrow(() -> new IllegalStateException("Salt not found"));
                // Ingredient lemon = ingredientRepository.findByNameIgnoreCase("Lemon")
                // .orElseThrow(() -> new IllegalStateException("Lemon not found"));
                Ingredient lettuce = ingredientRepository.findByNameIgnoreCase("Lettuce")
                                .orElseThrow(() -> new IllegalStateException("Lettuce not found"));
                // Ingredient cheese = ingredientRepository.findByNameIgnoreCase("Cheddar
                // Cheese")
                // .orElseThrow(() -> new IllegalStateException("Cheddar Cheese not found"));
                // Ingredient butter = ingredientRepository.findByNameIgnoreCase("Butter")
                // .orElseThrow(() -> new IllegalStateException("Butter not found"));
                Ingredient beefShank = ingredientRepository.findByNameIgnoreCase("Beef Shank")
                                .orElseThrow(() -> new IllegalStateException("Beef Shank not found"));
                Ingredient riceNoodles = ingredientRepository.findByNameIgnoreCase("Rice Noodles")
                                .orElseThrow(() -> new IllegalStateException("Rice Noodles not found"));
                Ingredient ginger = ingredientRepository.findByNameIgnoreCase("Ginger")
                                .orElseThrow(() -> new IllegalStateException("Ginger not found"));
                Ingredient greenOnion = ingredientRepository.findByNameIgnoreCase("Green Onion")
                                .orElseThrow(() -> new IllegalStateException("Green Onion not found"));
                Ingredient starAnise = ingredientRepository.findByNameIgnoreCase("Star Anise")
                                .orElseThrow(() -> new IllegalStateException("Star Anise not found"));
                Ingredient cinnamonStick = ingredientRepository.findByNameIgnoreCase("Cinnamon Stick")
                                .orElseThrow(() -> new IllegalStateException("Cinnamon Stick not found"));
                Ingredient fishSauce = ingredientRepository.findByNameIgnoreCase("Fish Sauce")
                                .orElseThrow(() -> new IllegalStateException("Fish Sauce not found"));
                Ingredient lemongrass = ingredientRepository.findByNameIgnoreCase("Lemongrass")
                                .orElseThrow(() -> new IllegalStateException("Lemongrass not found"));
                Ingredient chili = ingredientRepository.findByNameIgnoreCase("Chili")
                                .orElseThrow(() -> new IllegalStateException("Chili not found"));
                Ingredient waterSpinach = ingredientRepository.findByNameIgnoreCase("Water Spinach")
                                .orElseThrow(() -> new IllegalStateException("Water Spinach not found"));
                Ingredient tofu = ingredientRepository.findByNameIgnoreCase("Tofu")
                                .orElseThrow(() -> new IllegalStateException("Tofu not found"));
                Ingredient groundPork = ingredientRepository.findByNameIgnoreCase("Ground Pork")
                                .orElseThrow(() -> new IllegalStateException("Ground Pork not found"));
                Ingredient ricePaper = ingredientRepository.findByNameIgnoreCase("Rice Paper")
                                .orElseThrow(() -> new IllegalStateException("Rice Paper not found"));
                Ingredient woodEarMushroom = ingredientRepository.findByNameIgnoreCase("Wood Ear Mushroom")
                                .orElseThrow(() -> new IllegalStateException("Wood Ear Mushroom not found"));
                Ingredient beanSprouts = ingredientRepository.findByNameIgnoreCase("Bean Sprouts")
                                .orElseThrow(() -> new IllegalStateException("Bean Sprouts not found"));
                Ingredient cilantro = ingredientRepository.findByNameIgnoreCase("Cilantro")
                                .orElseThrow(() -> new IllegalStateException("Cilantro not found"));
                Ingredient vegetableOil = ingredientRepository.findByNameIgnoreCase("Vegetable Oil")
                                .orElseThrow(() -> new IllegalStateException("Vegetable Oil not found"));
                Ingredient soySauce = ingredientRepository.findByNameIgnoreCase("Soy Sauce")
                                .orElseThrow(() -> new IllegalStateException("Soy Sauce not found"));
                Ingredient porkBelly = ingredientRepository.findByNameIgnoreCase("Pork Belly")
                                .orElseThrow(() -> new IllegalStateException("Pork Belly not found"));
                Ingredient coconutWater = ingredientRepository.findByNameIgnoreCase("Coconut Water")
                                .orElseThrow(() -> new IllegalStateException("Coconut Water not found"));
                Ingredient oysterSauce = ingredientRepository.findByNameIgnoreCase("Oyster Sauce")
                                .orElseThrow(() -> new IllegalStateException("Oyster Sauce not found"));
                Ingredient beefSirloin = ingredientRepository.findByNameIgnoreCase("Beef Sirloin")
                                .orElseThrow(() -> new IllegalStateException("Beef Sirloin not found"));
                Ingredient catfish = ingredientRepository.findByNameIgnoreCase("Catfish")
                                .orElseThrow(() -> new IllegalStateException("Catfish not found"));
                Ingredient tamarindPaste = ingredientRepository.findByNameIgnoreCase("Tamarind Paste")
                                .orElseThrow(() -> new IllegalStateException("Tamarind Paste not found"));
                Ingredient okra = ingredientRepository.findByNameIgnoreCase("Okra")
                                .orElseThrow(() -> new IllegalStateException("Okra not found"));
                Ingredient shrimp = ingredientRepository.findByNameIgnoreCase("Shrimp")
                                .orElseThrow(() -> new IllegalStateException("Shrimp not found"));
                Ingredient peanuts = ingredientRepository.findByNameIgnoreCase("Peanuts")
                                .orElseThrow(() -> new IllegalStateException("Peanuts not found"));
                Ingredient peas = ingredientRepository.findByNameIgnoreCase("Peas")
                                .orElseThrow(() -> new IllegalStateException("Peas not found"));
                Ingredient thickRiceNoodles = ingredientRepository.findByNameIgnoreCase("Thick Rice Noodles")
                                .orElseThrow(() -> new IllegalStateException("Thick Rice Noodles not found"));
                Ingredient porkHock = ingredientRepository.findByNameIgnoreCase("Pork Hock")
                                .orElseThrow(() -> new IllegalStateException("Pork Hock not found"));
                Ingredient shrimpPaste = ingredientRepository.findByNameIgnoreCase("Shrimp Paste")
                                .orElseThrow(() -> new IllegalStateException("Shrimp Paste not found"));
                Ingredient porkChop = ingredientRepository.findByNameIgnoreCase("Pork Chop")
                                .orElseThrow(() -> new IllegalStateException("Pork Chop not found"));
                Ingredient brokenRice = ingredientRepository.findByNameIgnoreCase("Broken Rice")
                                .orElseThrow(() -> new IllegalStateException("Broken Rice not found"));
                Ingredient honey = ingredientRepository.findByNameIgnoreCase("Honey")
                                .orElseThrow(() -> new IllegalStateException("Honey not found"));
                Ingredient sweetCorn = ingredientRepository.findByNameIgnoreCase("Sweet Corn")
                                .orElseThrow(() -> new IllegalStateException("Sweet Corn not found"));
                Ingredient cornstarch = ingredientRepository.findByNameIgnoreCase("Cornstarch")
                                .orElseThrow(() -> new IllegalStateException("Cornstarch not found"));
                Ingredient condensedMilk = ingredientRepository.findByNameIgnoreCase("Condensed Milk")
                                .orElseThrow(() -> new IllegalStateException("Condensed Milk not found"));
                Ingredient vanillaExtract = ingredientRepository.findByNameIgnoreCase("Vanilla Extract")
                                .orElseThrow(() -> new IllegalStateException("Vanilla Extract not found"));
                Ingredient chiliOil = ingredientRepository.findByNameIgnoreCase("Chili Oil")
                                .orElseThrow(() -> new IllegalStateException("Chili Oil not found"));

                // Lấy hoặc tạo category
                RecipeCategory asianCat = recipeCategoryRepository.findByNameIgnoreCase("Asian")
                                .orElseThrow(() -> new IllegalStateException("Asian category not found"));
                RecipeCategory vegCat = recipeCategoryRepository.findByNameIgnoreCase("Vegetarian")
                                .orElseThrow(() -> new IllegalStateException("Vegetarian category not found"));
                RecipeCategory healthyCat = recipeCategoryRepository.findByNameIgnoreCase("Healthy")
                                .orElseThrow(() -> new IllegalStateException("Healthy category not found"));
                RecipeCategory italianCat = recipeCategoryRepository.findByNameIgnoreCase("Italian")
                                .orElseThrow(() -> new IllegalStateException("Italian category not found"));
                RecipeCategory saladCat = recipeCategoryRepository.findByNameIgnoreCase("Salad")
                                .orElseThrow(() -> new IllegalStateException("Salad category not found"));
                RecipeCategory quickCat = recipeCategoryRepository.findByNameIgnoreCase("Quick & Easy")
                                .orElseThrow(() -> new IllegalStateException("Quick & Easy category not found"));
                RecipeCategory vietnameseCat = recipeCategoryRepository.findByNameIgnoreCase("Vietnamese")
                                .orElseThrow(() -> new IllegalStateException("Vietnamese category not found"));
                RecipeCategory appetizerCat = recipeCategoryRepository.findByNameIgnoreCase("Appetizer")
                                .orElseThrow(() -> new IllegalStateException("Appetizer category not found"));
                RecipeCategory soupCat = recipeCategoryRepository.findByNameIgnoreCase("Soup")
                                .orElseThrow(() -> new IllegalStateException("Soup category not found"));
                RecipeCategory dessertCat = recipeCategoryRepository.findByNameIgnoreCase("Dessert")
                                .orElseThrow(() -> new IllegalStateException("Dessert category not found"));
                RecipeCategory seafoodCat = recipeCategoryRepository.findByNameIgnoreCase("Seafood")
                                .orElseThrow(() -> new IllegalStateException("Seafood category not found"));
                RecipeCategory highProteinCat = recipeCategoryRepository.findByNameIgnoreCase("High Protein")
                                .orElseThrow(() -> new IllegalStateException("High Protein category not found"));
                RecipeCategory noodlesCat = recipeCategoryRepository.findByNameIgnoreCase("Noodles")
                                .orElseThrow(() -> new IllegalStateException("Noodles category not found"));
                RecipeCategory grilledCat = recipeCategoryRepository.findByNameIgnoreCase("Grilled")
                                .orElseThrow(() -> new IllegalStateException("Grilled category not found"));
                RecipeCategory friedCat = recipeCategoryRepository.findByNameIgnoreCase("Fried")
                                .orElseThrow(() -> new IllegalStateException("Fried category not found"));
                RecipeCategory spicyCat = recipeCategoryRepository.findByNameIgnoreCase("Spicy")
                                .orElseThrow(() -> new IllegalStateException("Spicy category not found"));

                /* ----------------- Recipe 1: Chicken Rice Bowl ----------------- */
                Recipe chickenRice = new Recipe();
                chickenRice.setCreatedBy(admin);
                chickenRice.setTitle("Chicken Rice Bowl");
                chickenRice.setDescription("A healthy and balanced meal with chicken, rice, and broccoli.");
                chickenRice.setInstructions("""
                                    1. Cook rice.
                                    2. Grill chicken.
                                    3. Steam broccoli.
                                    4. Combine all and serve.
                                """);
                chickenRice.setCookingTimeMinutes(30);
                chickenRice.setImageUrl("https://modernmealmakeover.com/wp-content/uploads/2020/10/IMG_6548-4.jpg");
                chickenRice.setStatus(RecipeStatus.PUBLISHED);
                chickenRice.setRole(MealRole.MAIN_DISH);
                chickenRice.setMealType(MealType.LUNCH);
                chickenRice.getCategories().add(asianCat);
                chickenRice.getCategories().add(healthyCat);

                chickenRice.addIngredient(new RecipeIngredient(null, chickenRice, chicken, 150.0, IngredientUnit.G));
                chickenRice.addIngredient(new RecipeIngredient(null, chickenRice, rice, 200.0, IngredientUnit.G));
                chickenRice.addIngredient(new RecipeIngredient(null, chickenRice, broccoli, 100.0, IngredientUnit.G));

                /* ----------------- Recipe 2: Broccoli Stir-Fry ----------------- */
                Recipe broccoliStirFry = new Recipe();
                broccoliStirFry.setCreatedBy(admin);
                broccoliStirFry.setTitle("Broccoli Stir-Fry");
                broccoliStirFry.setDescription("Simple vegetarian stir-fry with broccoli and rice.");
                broccoliStirFry.setInstructions("""
                                    1. Stir-fry broccoli with garlic.
                                    2. Add soy sauce.
                                    3. Serve with rice.
                                """);
                broccoliStirFry.setCookingTimeMinutes(20);
                broccoliStirFry.setImageUrl("https://tse3.mm.bing.net/th/id/OIP.XMHmcJmCzay3pdxzhXoYsAAAAA?rs=1&pid=ImgDetMain&o=7&rm=3");
                broccoliStirFry.setStatus(RecipeStatus.PUBLISHED);
                broccoliStirFry.setRole(MealRole.SIDE_DISH);
                broccoliStirFry.setMealType(MealType.DINNER);
                broccoliStirFry.getCategories().add(asianCat);
                broccoliStirFry.getCategories().add(vegCat);

                broccoliStirFry.addIngredient(
                                new RecipeIngredient(null, broccoliStirFry, broccoli, 200.0, IngredientUnit.G));
                broccoliStirFry.addIngredient(
                                new RecipeIngredient(null, broccoliStirFry, rice, 150.0, IngredientUnit.G));

                /* ----------------- Recipe 4: Spaghetti Bolognese ----------------- */
                Recipe bolognese = new Recipe();
                bolognese.setCreatedBy(admin);
                bolognese.setTitle("Spaghetti Bolognese");
                bolognese.setDescription("A classic Italian dish with a rich meat sauce.");
                bolognese.setInstructions("""
                                    1. Heat olive oil in a pan.
                                    2. Sauté onion and garlic (optional).
                                    3. Add ground beef and brown it.
                                    4. Stir in chopped tomatoes and herbs. Simmer for 30 minutes.
                                    5. Serve over cooked pasta (e.g., rice, for this demo).
                                """);
                bolognese.setCookingTimeMinutes(45);
                bolognese.setImageUrl("https://res-console.cloudinary.com/dmfvnmpuq/thumbnails/v1/image/upload/v1767460533/NDU2ZGY5MjgtNTViNy00ZWQxLWEyZDQtYzExMWE5ZTY1Nzkx/drilldown");
                bolognese.setStatus(RecipeStatus.PUBLISHED);
                bolognese.setRole(MealRole.MAIN_DISH);
                bolognese.setMealType(MealType.DINNER);
                bolognese.getCategories().add(italianCat);

                // Thêm nguyên liệu cho món này
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, groundBeef, 250.0, IngredientUnit.G));
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, tomato, 400.0, IngredientUnit.G));
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, onion, 100.0, IngredientUnit.G));
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, oliveOil, 15.0, IngredientUnit.ML));
                // Giả sử dùng "Rice" thay cho "Pasta" để demo
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, rice, 150.0, IngredientUnit.G));

                /* ----------------- Recipe 5: Simple Chicken Salad ----------------- */
                Recipe chickenSalad = new Recipe();
                chickenSalad.setCreatedBy(admin);
                chickenSalad.setTitle("Simple Chicken Salad");
                chickenSalad.setDescription("A light and healthy chicken salad.");
                chickenSalad.setInstructions("""
                                    1. Grill chicken breast until cooked, then slice.
                                    2. Wash and chop broccoli and carrots.
                                    3. Mix all ingredients in a bowl.
                                    4. Drizzle with olive oil.
                                """);
                chickenSalad.setCookingTimeMinutes(15);
                chickenSalad.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767460759/26973a8d-f4ba-4641-aaf5-814ad39d637d.png");
                chickenSalad.setStatus(RecipeStatus.PUBLISHED);
                chickenSalad.setRole(MealRole.MAIN_DISH);
                chickenSalad.setMealType(MealType.LUNCH);
                chickenSalad.getCategories().add(healthyCat);
                chickenSalad.getCategories().add(saladCat);
                chickenSalad.getCategories().add(quickCat);

                // Thêm nguyên liệu cho món này
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, chicken, 150.0, IngredientUnit.G));
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, broccoli, 100.0, IngredientUnit.G));
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, carrot, 50.0, IngredientUnit.G));
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, oliveOil, 10.0, IngredientUnit.ML));

                /* ----------------- Recipe 6: Roasted Vegetables ----------------- */
                Recipe roastedVegs = new Recipe();
                roastedVegs.setCreatedBy(admin);
                roastedVegs.setTitle("Roasted Vegetables");
                roastedVegs.setDescription("Simple and healthy roasted side dish.");
                roastedVegs.setInstructions("""
                                    1. Preheat oven to 200°C (400°F).
                                    2. Chop broccoli, carrots, and onions.
                                    3. Toss with olive oil, salt, and pepper.
                                    4. Spread on a baking sheet and roast for 20-25 minutes.
                                """);
                roastedVegs.setCookingTimeMinutes(30);
                roastedVegs.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767460629/33ad6f13-efa0-409e-a931-877596695522.png");
                roastedVegs.setStatus(RecipeStatus.PUBLISHED);
                roastedVegs.setRole(MealRole.SIDE_DISH);
                roastedVegs.setMealType(MealType.DINNER);
                roastedVegs.getCategories().add(healthyCat);
                roastedVegs.getCategories().add(vegCat); // Món này chay
                roastedVegs.getCategories().add(quickCat);

                // Thêm nguyên liệu cho món này
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, broccoli, 200.0, IngredientUnit.G));
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, carrot, 100.0, IngredientUnit.G));
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, onion, 50.0, IngredientUnit.G));
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, oliveOil, 15.0, IngredientUnit.ML));

                /* ----------------- Recipe 7: Pho Bo (Pho) ----------------- */
                Recipe phoBo = new Recipe();
                phoBo.setCreatedBy(admin);
                phoBo.setTitle("Hanoi Beef Pho");
                phoBo.setDescription("Traditional beef noodle soup with clear, sweet broth.");
                phoBo.setInstructions("""
                                    1. Wash beef shank. Grill ginger and shallots.
                                    2. Put beef bones, beef shank, grilled ginger, grilled shallots, star anise, and cinnamon into a pot of water.
                                    3. Simmer on low heat for 2-3 hours. Season with salt and fish sauce.
                                    4. Blanch pho noodles and bean sprouts.
                                    5. Thinly slice the beef shank, arrange in a bowl with noodles, green onions, and cilantro. Pour in the broth.
                                """);
                phoBo.setCookingTimeMinutes(180);
                phoBo.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461337/58ce753f-159d-42e1-8551-6d73563b1040.png");
                phoBo.setStatus(RecipeStatus.PUBLISHED);
                phoBo.setRole(MealRole.MAIN_DISH);
                phoBo.setMealType(MealType.BREAKFAST);
                phoBo.getCategories().add(vietnameseCat);
                phoBo.getCategories().add(asianCat);

                phoBo.addIngredient(new RecipeIngredient(null, phoBo, beefShank, 200.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, riceNoodles, 150.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, onion, 50.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, ginger, 10.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, starAnise, 2.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, cinnamonStick, 3.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, greenOnion, 10.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, cilantro, 5.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, beanSprouts, 30.0, IngredientUnit.G));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, fishSauce, 15.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 8: Ga Xao Sa Ot (Lemongrass Chicken)
                 * -----------------
                 */
                Recipe gaXaoSaOt = new Recipe();
                gaXaoSaOt.setCreatedBy(admin);
                gaXaoSaOt.setTitle("Lemongrass Chili Chicken");
                gaXaoSaOt.setDescription("Tender chicken, spicy lemongrass and chili flavor, savory and perfect with rice.");
                gaXaoSaOt.setInstructions("""
                                    1. Thinly slice chicken breast. Mince lemongrass, chili, and garlic.
                                    2. Sauté garlic, lemongrass, and chili in oil until fragrant.
                                    3. Add chicken and stir-fry until firm.
                                    4. Season with fish sauce, sugar, and salt. Stir well until chicken is cooked.
                                """);
                gaXaoSaOt.setCookingTimeMinutes(20);
                gaXaoSaOt.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461384/b9be6baf-1352-4edd-b7f0-655b6349252c.png"); // Ảnh minh họa
                gaXaoSaOt.setStatus(RecipeStatus.PUBLISHED);
                gaXaoSaOt.setRole(MealRole.MAIN_DISH);
                gaXaoSaOt.setMealType(MealType.DINNER);
                gaXaoSaOt.getCategories().add(vietnameseCat);
                gaXaoSaOt.getCategories().add(quickCat);

                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, chicken, 200.0, IngredientUnit.G));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, lemongrass, 30.0, IngredientUnit.G));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, chili, 10.0, IngredientUnit.G));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, garlic, 5.0, IngredientUnit.G));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, fishSauce, 15.0, IngredientUnit.ML));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, sugar, 5.0, IngredientUnit.G));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, vegetableOil, 10.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 9: Rau Muong Xao Toi (Stir-fried Water Spinach)
                 * -----------------
                 */
                Recipe rauMuongXaoToi = new Recipe();
                rauMuongXaoToi.setCreatedBy(admin);
                rauMuongXaoToi.setTitle("Stir-fried Water Spinach with Garlic");
                rauMuongXaoToi.setDescription("A national vegetable dish, green and crunchy, fragrant with garlic.");
                rauMuongXaoToi.setInstructions("""
                                    1. Pick and wash water spinach. Crush garlic.
                                    2. Sauté garlic with oil until fragrant.
                                    3. Turn up heat to high, add water spinach and stir-fry quickly.
                                    4. Season with fish sauce, salt, and sugar. Turn off heat.
                                """);
                rauMuongXaoToi.setCookingTimeMinutes(10);
                rauMuongXaoToi.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461429/7f1795cd-0087-482e-a37f-61a007453ca4.png"); // Ảnh minh
                                                                                                            // họa
                rauMuongXaoToi.setStatus(RecipeStatus.PUBLISHED);
                rauMuongXaoToi.setRole(MealRole.SIDE_DISH);
                rauMuongXaoToi.setMealType(MealType.LUNCH);
                rauMuongXaoToi.getCategories().add(vietnameseCat);
                rauMuongXaoToi.getCategories().add(quickCat);
                rauMuongXaoToi.getCategories().add(vegCat);

                rauMuongXaoToi.addIngredient(
                                new RecipeIngredient(null, rauMuongXaoToi, waterSpinach, 300.0, IngredientUnit.G));
                rauMuongXaoToi.addIngredient(
                                new RecipeIngredient(null, rauMuongXaoToi, garlic, 15.0, IngredientUnit.G));
                rauMuongXaoToi.addIngredient(
                                new RecipeIngredient(null, rauMuongXaoToi, fishSauce, 10.0, IngredientUnit.ML));
                rauMuongXaoToi.addIngredient(
                                new RecipeIngredient(null, rauMuongXaoToi, vegetableOil, 15.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 10: Dau Hu Sot Ca (Tofu in Tomato Sauce)
                 * -----------------
                 */
                Recipe dauHuSotCa = new Recipe();
                dauHuSotCa.setCreatedBy(admin);
                dauHuSotCa.setTitle("Tofu in Tomato Sauce");
                dauHuSotCa.setDescription("Soft and creamy tofu in rich tomato sauce, served with white rice.");
                dauHuSotCa.setInstructions("""
                                    1. Cut tofu into bite-sized pieces, fry until golden brown.
                                    2. Mince tomatoes, sauté shallots/garlic until fragrant.
                                    3. Add tomatoes and stir-fry until soft, add water, season with fish sauce, salt, and sugar.
                                    4. Add fried tofu, simmer on low heat for 10 minutes.
                                    5. Sprinkle with green onions, cilantro and turn off heat.
                                """);
                dauHuSotCa.setCookingTimeMinutes(25);
                dauHuSotCa.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461491/5e3853b5-95e1-4f86-9911-5758dea81924.png"); // Ảnh minh họa
                dauHuSotCa.setStatus(RecipeStatus.PUBLISHED);
                dauHuSotCa.setRole(MealRole.MAIN_DISH);
                dauHuSotCa.setMealType(MealType.DINNER);
                dauHuSotCa.getCategories().add(vietnameseCat);
                dauHuSotCa.getCategories().add(vegCat);

                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, tofu, 200.0, IngredientUnit.G));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, tomato, 150.0, IngredientUnit.G));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, greenOnion, 10.0, IngredientUnit.G));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, cilantro, 5.0, IngredientUnit.G));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, fishSauce, 10.0, IngredientUnit.ML));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, vegetableOil, 20.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 11: Nem Ran (Fried Spring Rolls) -----------------
                 */
                Recipe nemRan = new Recipe();
                nemRan.setCreatedBy(admin);
                nemRan.setTitle("Fried Spring Rolls (Nem Ran)");
                nemRan.setDescription("Crispy skin, fragrant meat and vegetable filling, dipped in sweet and sour fish sauce.");
                nemRan.setInstructions("""
                                        1. Mince pork, wood ear mushrooms, carrots. Mix well with eggs, bean sprouts, vermicelli (if any).
                                        2. Season with fish sauce, salt, and sugar.
                                        3. Spread rice paper, add filling and roll up.
                                        4. Deep fry over medium heat until golden and crispy.
                                """);
                nemRan.setCookingTimeMinutes(40);
                nemRan.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461581/5801653a-23b7-4adf-9583-b44bf814b6a3.png");
                nemRan.setStatus(RecipeStatus.PUBLISHED);
                nemRan.setRole(MealRole.MAIN_DISH);
                nemRan.setMealType(MealType.LUNCH);
                nemRan.getCategories().add(vietnameseCat);
                nemRan.getCategories().add(appetizerCat);

                // === ĐỊNH LƯỢNG CHO 1 NGƯỜI (4-5 cuốn) ===

                nemRan.addIngredient(new RecipeIngredient(null, nemRan, groundPork, 100.0, IngredientUnit.G));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, ricePaper, 40.0, IngredientUnit.G));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, egg, 1.0, IngredientUnit.EGG_PIECE));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, carrot, 30.0, IngredientUnit.G));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, woodEarMushroom, 10.0, IngredientUnit.G));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, beanSprouts, 20.0, IngredientUnit.G));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, fishSauce, 10.0, IngredientUnit.ML));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, vegetableOil, 100.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 12: Cơm chiên Dương Châu -----------------
                 */
                Recipe comChien = new Recipe();
                comChien.setTitle("Yangzhou Fried Rice");
                comChien.setDescription(
                                "Fluffy, golden fried rice combined with eggs, vegetables and Chinese sausage (using pork as substitute).");
                comChien.setInstructions("""
                                    1. Let white rice cool. Beat eggs.
                                    2. Sauté garlic/shallots until fragrant, add eggs and scramble.
                                    3. Add cold rice and stir-fry over high heat, stirring constantly.
                                    4. Add peas, carrots (blanched), and meat.
                                    5. Season with soy sauce, salt, and sugar. Sprinkle with green onions.
                                """);
                comChien.setCookingTimeMinutes(15);
                comChien.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461715/ea21fb5e-ffb0-4a19-8fcb-0e8d374b6914.png");
                comChien.setStatus(RecipeStatus.PUBLISHED);
                comChien.setRole(MealRole.MAIN_DISH);
                comChien.setMealType(MealType.LUNCH);
                comChien.getCategories().add(vietnameseCat);
                comChien.getCategories().add(asianCat);
                comChien.getCategories().add(quickCat);

                comChien.addIngredient(new RecipeIngredient(null, comChien, rice, 200.0, IngredientUnit.G));
                comChien.addIngredient(new RecipeIngredient(null, comChien, egg, 2.0, IngredientUnit.EGG_PIECE));
                comChien.addIngredient(new RecipeIngredient(null, comChien, carrot, 30.0, IngredientUnit.G));
                comChien.addIngredient(new RecipeIngredient(null, comChien, peas, 30.0, IngredientUnit.G));
                comChien.addIngredient(new RecipeIngredient(null, comChien, groundPork, 50.0, IngredientUnit.G)); // Dùng
                                                                                                                  // tạm
                comChien.addIngredient(new RecipeIngredient(null, comChien, soySauce, 15.0, IngredientUnit.ML));
                comChien.addIngredient(new RecipeIngredient(null, comChien, greenOnion, 10.0, IngredientUnit.G));

                /*
                 * ----------------- Recipe 13: Thit Kho Trung (Braised Pork with Eggs)
                 * -----------------
                 */
                Recipe thitKho = new Recipe();
                thitKho.setCreatedBy(admin);
                thitKho.setTitle("Braised Pork with Eggs");
                thitKho.setDescription("Traditional Tet dish, tender pork belly, eggs absorbing rich flavor.");
                thitKho.setInstructions("""
                                    1. Cut pork belly into square pieces, marinate with fish sauce, sugar, garlic, and shallots.
                                    2. Boil eggs, peel shells.
                                    3. Caramelize sugar to make color water.
                                    4. Stir-fry meat until firm, then add caramel water and fresh coconut water.
                                    5. Add boiled eggs. Braise on low heat for 1-2 hours until meat is tender.
                                """);
                thitKho.setCookingTimeMinutes(120);
                thitKho.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461788/e4dbe3ff-26e8-4611-a129-ca70b74271f6.png"); // Ảnh minh họa
                thitKho.setStatus(RecipeStatus.PUBLISHED);
                thitKho.setRole(MealRole.MAIN_DISH);
                thitKho.setMealType(MealType.DINNER);
                thitKho.getCategories().add(vietnameseCat);

                thitKho.addIngredient(new RecipeIngredient(null, thitKho, porkBelly, 300.0, IngredientUnit.G));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, egg, 4.0, IngredientUnit.EGG_PIECE));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, coconutWater, 200.0, IngredientUnit.ML));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, fishSauce, 30.0, IngredientUnit.ML));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, sugar, 20.0, IngredientUnit.G));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, garlic, 10.0, IngredientUnit.G));

                /* ----------------- Recipe 14: Bo Luc Lac (Shaking Beef) ----------------- */
                Recipe boLucLac = new Recipe();
                boLucLac.setCreatedBy(admin);
                boLucLac.setTitle("Shaking Beef (Bo Luc Lac)");
                boLucLac.setDescription("Diced beef tenderloin, stir-fried over high heat with onions and bell peppers, served with salad.");
                boLucLac.setInstructions("""
                                    1. Cut beef tenderloin into cubes, marinate with oyster sauce, soy sauce, and garlic.
                                    2. Slice onions and tomatoes.
                                    3. Sauté garlic until fragrant, add beef and stir-fry quickly over high heat (shake the pan).
                                    4. Add onions, stir quickly then turn off heat.
                                    5. Serve on a salad plate (lettuce, tomato).
                                """);
                boLucLac.setCookingTimeMinutes(20);
                boLucLac.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461834/b17a7934-15b7-4157-b72e-b53f791098ab.png"); // Ảnh minh họa
                boLucLac.setStatus(RecipeStatus.PUBLISHED);
                boLucLac.setRole(MealRole.MAIN_DISH);
                boLucLac.setMealType(MealType.DINNER);
                boLucLac.getCategories().add(vietnameseCat);
                boLucLac.getCategories().add(quickCat);

                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, beefSirloin, 200.0, IngredientUnit.G));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, onion, 50.0, IngredientUnit.G));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, lettuce, 100.0, IngredientUnit.G));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, tomato, 50.0, IngredientUnit.G));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, soySauce, 15.0, IngredientUnit.ML));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, oysterSauce, 10.0, IngredientUnit.ML));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, garlic, 10.0, IngredientUnit.G));

                /*
                 * ----------------- Recipe 15: Canh Chua Ca (Vietnamese Sour Fish Soup)
                 * -----------------
                 */
                Recipe canhChua = new Recipe();
                canhChua.setCreatedBy(admin);
                canhChua.setTitle("Sour Fish Soup (Canh Chua)");
                canhChua.setDescription(
                                "Fish soup with sour taste from tamarind, sweet from pineapple (using tomato as substitute), fragrant herbs.");
                canhChua.setInstructions("""
                                    1. Prepare fish, marinate with fish sauce.
                                    2. Boil water, add tamarind paste.
                                    3. Add fish and cook until done.
                                    4. Add tomatoes, okra, bean sprouts.
                                    5. Season with fish sauce, salt, sugar to balance sour-salty-sweet taste.
                                    6. Turn off heat, add green onions, cilantro, fried garlic.
                                """);
                canhChua.setCookingTimeMinutes(30);
                canhChua.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461935/0122d5be-df80-45f9-9fb5-517dba0b28f2.png"); // Ảnh minh họa
                canhChua.setStatus(RecipeStatus.PUBLISHED);
                canhChua.setRole(MealRole.SOUP);
                canhChua.setMealType(MealType.DINNER);
                canhChua.getCategories().add(vietnameseCat);
                canhChua.getCategories().add(soupCat);

                canhChua.addIngredient(new RecipeIngredient(null, canhChua, catfish, 200.0, IngredientUnit.G)); //
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, tamarindPaste, 30.0, IngredientUnit.G));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, tomato, 100.0, IngredientUnit.G));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, beanSprouts, 50.0, IngredientUnit.G));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, okra, 50.0, IngredientUnit.G));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, cilantro, 10.0, IngredientUnit.G));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, fishSauce, 20.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 16: Goi Cuon (Fresh Spring Rolls) -----------------
                 */
                Recipe goiCuon = new Recipe();
                goiCuon.setCreatedBy(admin);
                goiCuon.setTitle("Fresh Spring Rolls with Shrimp and Pork");
                goiCuon.setDescription("Refreshing appetizer, rice paper rolls with vermicelli, fresh herbs, shrimp, and pork.");
                goiCuon.setInstructions("""
                                        1. Boil shrimp and pork belly. Thinly slice pork, split shrimp in half.
                                        2. Blanch rice noodles.
                                        3. Dip rice paper in water to soften.
                                        4. Spread rice paper, arrange lettuce, cilantro, noodles, pork, and shrimp on top.
                                        5. Roll tightly. Serve with dipping sauce (peanut sauce).
                                """);
                goiCuon.setCookingTimeMinutes(25);
                goiCuon.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767461940/eb222bee-bd3b-4e2b-a856-6f8635aeff0b.png");
                goiCuon.setStatus(RecipeStatus.PUBLISHED);
                goiCuon.setRole(MealRole.SIDE_DISH);
                goiCuon.setMealType(MealType.SNACK);
                goiCuon.getCategories().add(vietnameseCat);
                goiCuon.getCategories().add(appetizerCat);
                goiCuon.getCategories().add(healthyCat);

                // === ĐỊNH LƯỢNG CHO 1 NGƯỜI (3-4 cuốn) ===

                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, shrimp, 50.0, IngredientUnit.G));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, porkBelly, 50.0, IngredientUnit.G));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, ricePaper, 40.0, IngredientUnit.G));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, riceNoodles, 80.0, IngredientUnit.G));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, lettuce, 50.0, IngredientUnit.G));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, cilantro, 10.0, IngredientUnit.G));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, peanuts, 10.0, IngredientUnit.G));

                // /* ----------------- Recipe 17: Bun Bo Hue ----------------- */
                Recipe bunBoHue = new Recipe();
                bunBoHue.setCreatedBy(admin);
                bunBoHue.setTitle("Hue Beef Noodle Soup (Bun Bo Hue)");
                bunBoHue.setDescription("Rich broth, spicy lemongrass flavor and characteristic Hue shrimp paste.");
                bunBoHue.setInstructions(
                                """
                                                    1. Stew beef shank and pork hock with crushed lemongrass.
                                                    2. Sauté lemongrass, garlic, chili, then add diluted shrimp paste and stir-fry.
                                                    3. Add the lemongrass chili mixture to the stew pot, season with fish sauce, salt, and sugar.
                                                    4. Blanch noodles, arrange meat, pork hock, sprinkle green onions. Pour broth and add chili oil.
                                                """);
                bunBoHue.setCookingTimeMinutes(150);
                bunBoHue.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462093/cf7a8e25-b5eb-4b4d-b1ea-e40c012ce88b.png"); // Dùng lại ảnh
                                                                                                      // Phở
                bunBoHue.setStatus(RecipeStatus.PUBLISHED);
                bunBoHue.setRole(MealRole.MAIN_DISH);
                bunBoHue.setMealType(MealType.BREAKFAST);
                bunBoHue.getCategories().add(vietnameseCat);
                bunBoHue.getCategories().add(soupCat);

                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, thickRiceNoodles, 150.0, IngredientUnit.G));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, beefShank, 100.0, IngredientUnit.G));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, porkHock, 100.0, IngredientUnit.G));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, lemongrass, 50.0, IngredientUnit.G));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, shrimpPaste, 10.0, IngredientUnit.G));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, chiliOil, 5.0, IngredientUnit.G));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, fishSauce, 15.0, IngredientUnit.ML));

                // /* ----------------- Recipe 18: Com Tam Suon (Broken Rice) -----------------
                // */
                Recipe comTam = new Recipe();
                comTam.setCreatedBy(admin);
                comTam.setTitle("Broken Rice with Grilled Pork Chop");
                comTam.setDescription("Fragrant honey-grilled pork chop, served with broken rice and sweet and sour fish sauce.");
                comTam.setInstructions("""
                                    1. Marinate pork chops with fish sauce, honey, garlic, and shallots.
                                    2. Grill chops over charcoal or in an oven until charred and golden.
                                    3. Cook broken rice.
                                    4. Make sweet and sour fish sauce (fish sauce, sugar, lemon/vinegar, garlic, chili).
                                    5. Serve rice, chops, scallion oil (green onion + oil), and fish sauce.
                                """);
                comTam.setCookingTimeMinutes(45);
                comTam.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462162/7ed9b789-55c1-4541-8fc1-f0a835e9b0af.png"); // Ảnh minh họa
                comTam.setStatus(RecipeStatus.PUBLISHED);
                comTam.setRole(MealRole.MAIN_DISH);
                comTam.setMealType(MealType.LUNCH);
                comTam.getCategories().add(vietnameseCat);

                comTam.addIngredient(new RecipeIngredient(null, comTam, porkChop, 150.0, IngredientUnit.G));
                comTam.addIngredient(new RecipeIngredient(null, comTam, brokenRice, 150.0, IngredientUnit.G));
                comTam.addIngredient(new RecipeIngredient(null, comTam, fishSauce, 20.0, IngredientUnit.ML));
                comTam.addIngredient(new RecipeIngredient(null, comTam, honey, 10.0, IngredientUnit.G));
                comTam.addIngredient(new RecipeIngredient(null, comTam, garlic, 5.0, IngredientUnit.G));
                comTam.addIngredient(new RecipeIngredient(null, comTam, greenOnion, 10.0, IngredientUnit.G));

                /*
                 * ----------------- Recipe 19: Sup Ga Ngo Non (Chicken Corn Soup)
                 * -----------------
                 */
                Recipe supGaNgo = new Recipe();
                supGaNgo.setCreatedBy(admin);
                supGaNgo.setTitle("Chicken Corn Soup");
                supGaNgo.setDescription("Sweet and light appetizer soup, slightly thick, with shredded chicken and corn kernels.");
                supGaNgo.setInstructions("""
                                    1. Boil chicken breast, keep the broth. Shred the chicken.
                                    2. Add sweet corn and shredded chicken to the broth, bring to a boil.
                                    3. Season with salt and sugar.
                                    4. Mix cornstarch with water, pour slowly into the pot, stirring well to thicken the soup.
                                    5. Beat eggs, pour slowly into the soup to create ribbons.
                                    6. Sprinkle with green onions, cilantro, and pepper.
                                """);
                supGaNgo.setCookingTimeMinutes(25);
                supGaNgo.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462215/149fc974-771b-4f0d-868b-80ef8bfd1ab2.png"); // Ảnh minh họa
                supGaNgo.setStatus(RecipeStatus.PUBLISHED);
                supGaNgo.setRole(MealRole.SOUP);
                supGaNgo.setMealType(MealType.SNACK);
                supGaNgo.getCategories().add(soupCat);
                supGaNgo.getCategories().add(appetizerCat);
                supGaNgo.getCategories().add(asianCat);

                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, chicken, 100.0, IngredientUnit.G));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, sweetCorn, 100.0, IngredientUnit.G));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, egg, 1.0, IngredientUnit.EGG_PIECE));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, cornstarch, 15.0, IngredientUnit.G));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, cilantro, 5.0, IngredientUnit.G));

                /* ----------------- Recipe 20: Banh Flan (Caramel Custard) ----------------- */
                Recipe banhFlan = new Recipe();
                banhFlan.setCreatedBy(admin);
                banhFlan.setTitle("Caramel Custard (Banh Flan)");
                banhFlan.setDescription("Smooth, creamy dessert with rich egg and milk flavor, fragrant caramel.");
                banhFlan.setInstructions("""
                                    1. Caramelize sugar with water, pour into the bottom of molds.
                                    2. Beat eggs.
                                    3. Warm up fresh milk and condensed milk. Do not boil.
                                    4. Slowly pour milk mixture into eggs, stir well. Add vanilla.
                                    5. Strain mixture through a sieve, pour into molds with caramel.
                                    6. Steam or bake in a water bath at 150°C for 40-50 minutes.
                                    7. Let cool and refrigerate before serving.
                                """);
                banhFlan.setCookingTimeMinutes(60);
                banhFlan.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462250/e093d21f-4da3-452f-aa57-e9bc3ae7f9c4.png");
                banhFlan.setStatus(RecipeStatus.PUBLISHED);
                banhFlan.setRole(MealRole.DESSERT);
                banhFlan.setMealType(MealType.SNACK);
                banhFlan.getCategories().add(dessertCat);

                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, egg, 4.0, IngredientUnit.EGG_PIECE));
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, milk, 300.0, IngredientUnit.ML));
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, condensedMilk, 100.0, IngredientUnit.G));
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, sugar, 50.0, IngredientUnit.G)); // Để làm
                                                                                                             // caramel
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, vanillaExtract, 5.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 21: Banh Mi (Vietnamese Sandwich) -----------------
                 */
                Recipe banhMi = new Recipe();
                banhMi.setCreatedBy(admin);
                banhMi.setTitle("Vietnamese Meat Sandwich (Banh Mi)");
                banhMi.setDescription("Crispy baguette with pate, meatballs, sausage, and pickled vegetables.");
                banhMi.setInstructions("""
                                    1. Toast baguette until crispy.
                                    2. Spread pate on the bread.
                                    3. Arrange meat (using pork belly substitute), vegetables, and cucumber.
                                    4. Add soy sauce, mayonnaise (using milk substitute), chili sauce (chili oil).
                                    5. Sprinkle with green onions and cilantro.
                                """);
                banhMi.setCookingTimeMinutes(15);
                banhMi.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462335/45ad1488-5e31-491c-8f03-69b6703227fa.png");
                banhMi.setStatus(RecipeStatus.PUBLISHED);
                banhMi.setRole(MealRole.MAIN_DISH);
                banhMi.setMealType(MealType.BREAKFAST);
                banhMi.getCategories().add(vietnameseCat);
                banhMi.getCategories().add(quickCat);

                banhMi.addIngredient(new RecipeIngredient(null, banhMi, porkBelly, 100.0, IngredientUnit.G));
                banhMi.addIngredient(new RecipeIngredient(null, banhMi, lettuce, 30.0, IngredientUnit.G));
                banhMi.addIngredient(new RecipeIngredient(null, banhMi, cilantro, 10.0, IngredientUnit.G));
                banhMi.addIngredient(new RecipeIngredient(null, banhMi, soySauce, 10.0, IngredientUnit.ML));

                /*
                 * ----------------- Recipe 22: Xoi Ga (Sticky Rice with Chicken)
                 * -----------------
                 */
                Recipe xoiGa = new Recipe();
                xoiGa.setCreatedBy(admin);
                xoiGa.setTitle("Sticky Rice with Chicken");
                xoiGa.setDescription("Soft and fragrant sticky rice, shredded chicken, fried shallots, savory soy sauce.");
                xoiGa.setInstructions("""
                                    1. Steam sticky rice after soaking overnight.
                                    2. Boil chicken breast, shred into small strips.
                                    3. Fry shallots with oil until fragrant.
                                    4. Presentation: sticky rice, shredded chicken, scallion oil, sprinkle with Vietnamese coriander (using cilantro substitute).
                                    5. Serve with mixed soy sauce (soy sauce + sugar).
                                """);
                xoiGa.setCookingTimeMinutes(40);
                xoiGa.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462521/377df28c-ba34-4041-9a23-d65965ee88a9.png");
                xoiGa.setStatus(RecipeStatus.PUBLISHED);
                xoiGa.setRole(MealRole.MAIN_DISH);
                xoiGa.setMealType(MealType.BREAKFAST);
                xoiGa.getCategories().add(vietnameseCat);
                xoiGa.getCategories().add(asianCat);

                xoiGa.addIngredient(new RecipeIngredient(null, xoiGa, rice, 180.0, IngredientUnit.G)); // glutinous rice
                                                                                                       // substitute
                xoiGa.addIngredient(new RecipeIngredient(null, xoiGa, chicken, 80.0, IngredientUnit.G));
                xoiGa.addIngredient(new RecipeIngredient(null, xoiGa, onion, 30.0, IngredientUnit.G));
                xoiGa.addIngredient(new RecipeIngredient(null, xoiGa, vegetableOil, 20.0, IngredientUnit.ML));
                xoiGa.addIngredient(new RecipeIngredient(null, xoiGa, soySauce, 15.0, IngredientUnit.ML));
                xoiGa.addIngredient(new RecipeIngredient(null, xoiGa, cilantro, 5.0, IngredientUnit.G));

                /*
                 * ----------------- Recipe 23: Che Buoi (Pomelo Sweet Soup) -----------------
                 */
                Recipe cheBuoi = new Recipe();
                cheBuoi.setCreatedBy(admin);
                cheBuoi.setTitle("Pomelo Sweet Soup");
                cheBuoi.setDescription("Refreshing sweet soup with pomelo pith, mung beans, and sweet coconut milk.");
                cheBuoi.setInstructions("""
                                    1. Prepare pomelo pith (using apple or other vegetable substitute).
                                    2. Cook coconut milk (using milk substitute) with sugar to make the soup base.
                                    3. Add pomelo pith, simmer gently.
                                    4. Add mung beans (using bean sprouts substitute).
                                    5. Cool down or eat hot as desired.
                                """);
                cheBuoi.setCookingTimeMinutes(20);
                cheBuoi.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462600/4e72df95-de49-4001-9abb-9bbde7bf58e2.png");
                cheBuoi.setStatus(RecipeStatus.PUBLISHED);
                cheBuoi.setRole(MealRole.DESSERT);
                cheBuoi.setMealType(MealType.SNACK);
                cheBuoi.getCategories().add(dessertCat);
                cheBuoi.getCategories().add(vietnameseCat);

                cheBuoi.addIngredient(new RecipeIngredient(null, cheBuoi, milk, 200.0, IngredientUnit.ML)); // Coconut
                                                                                                            // milk
                                                                                                            // substitute
                cheBuoi.addIngredient(new RecipeIngredient(null, cheBuoi, sugar, 40.0, IngredientUnit.G));
                cheBuoi.addIngredient(new RecipeIngredient(null, cheBuoi, beanSprouts, 50.0, IngredientUnit.G)); // Substitute
                cheBuoi.addIngredient(new RecipeIngredient(null, cheBuoi, cilantro, 20.0, IngredientUnit.G)); // Pomelo
                                                                                                              // substitute

                /* ----------------- Recipe 24: Mi Quang (Quang Noodles) ----------------- */
                Recipe miQuang = new Recipe();
                miQuang.setCreatedBy(admin);
                miQuang.setTitle("Quang Noodles");
                miQuang.setDescription("Central Vietnam specialty with yellow noodles, rich broth, delicious shrimp and pork.");
                miQuang.setInstructions("""
                                    1. Cook broth from pork bones and dried shrimp.
                                    2. Marinate shrimp and pork with turmeric and spices.
                                    3. Stir-fry shrimp and pork until fragrant.
                                    4. Blanch noodles, arrange in a bowl with fresh herbs.
                                    5. Pour in broth, add roasted peanuts and toasted rice paper.
                                """);
                miQuang.setCookingTimeMinutes(60);
                miQuang.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462694/bd9fe106-fea5-422b-8f73-515135065f91.png");
                miQuang.setStatus(RecipeStatus.PUBLISHED);
                miQuang.setRole(MealRole.MAIN_DISH);
                miQuang.setMealType(MealType.BREAKFAST);
                miQuang.getCategories().add(vietnameseCat);
                miQuang.getCategories().add(noodlesCat);
                miQuang.getCategories().add(seafoodCat);

                miQuang.addIngredient(new RecipeIngredient(null, miQuang, riceNoodles, 150.0, IngredientUnit.G));
                miQuang.addIngredient(new RecipeIngredient(null, miQuang, shrimp, 80.0, IngredientUnit.G));
                miQuang.addIngredient(new RecipeIngredient(null, miQuang, porkBelly, 80.0, IngredientUnit.G));
                miQuang.addIngredient(new RecipeIngredient(null, miQuang, peanuts, 20.0, IngredientUnit.G));
                miQuang.addIngredient(new RecipeIngredient(null, miQuang, lettuce, 50.0, IngredientUnit.G));
                miQuang.addIngredient(new RecipeIngredient(null, miQuang, fishSauce, 15.0, IngredientUnit.ML));

                /* ----------------- Recipe 25: Hu Tieu Nam Vang ----------------- */
                Recipe huTieu = new Recipe();
                huTieu.setCreatedBy(admin);
                huTieu.setTitle("Nam Vang Noodle Soup");
                huTieu.setDescription("Clear noodle soup, sweet and light with shrimp, meat, liver, and quail eggs.");
                huTieu.setInstructions("""
                                    1. Simmer pork bones for clear broth.
                                    2. Boil shrimp, minced meat balls, pork liver.
                                    3. Blanch noodles, place in a bowl.
                                    4. Arrange shrimp, meat, liver, bean sprouts, and green onions on top.
                                    5. Pour hot broth, serve with chili sauce.
                                """);
                huTieu.setCookingTimeMinutes(45);
                huTieu.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462700/88650938-fea5-4b97-9b1d-f61d7e7dc5e8.png");
                huTieu.setStatus(RecipeStatus.PUBLISHED);
                huTieu.setRole(MealRole.MAIN_DISH);
                huTieu.setMealType(MealType.BREAKFAST);
                huTieu.getCategories().add(vietnameseCat);
                huTieu.getCategories().add(noodlesCat);
                huTieu.getCategories().add(highProteinCat);

                huTieu.addIngredient(new RecipeIngredient(null, huTieu, riceNoodles, 150.0, IngredientUnit.G));
                huTieu.addIngredient(new RecipeIngredient(null, huTieu, shrimp, 60.0, IngredientUnit.G));
                huTieu.addIngredient(new RecipeIngredient(null, huTieu, groundPork, 80.0, IngredientUnit.G));
                huTieu.addIngredient(new RecipeIngredient(null, huTieu, beanSprouts, 50.0, IngredientUnit.G));
                huTieu.addIngredient(new RecipeIngredient(null, huTieu, greenOnion, 15.0, IngredientUnit.G));
                huTieu.addIngredient(new RecipeIngredient(null, huTieu, fishSauce, 15.0, IngredientUnit.ML));

                /* ----------------- Recipe 26: Bun Cha Ha Noi ----------------- */
                Recipe bunCha = new Recipe();
                bunCha.setCreatedBy(admin);
                bunCha.setTitle("Hanoi Bun Cha");
                bunCha.setDescription("Fragrant grilled pork, dipped in sweet and sour fish sauce, served with vermicelli and fresh herbs.");
                bunCha.setInstructions("""
                                    1. Marinate pork belly and minced pork with fish sauce, sugar, garlic.
                                    2. Grill meat over charcoal or in an oven.
                                    3. Make sweet and sour fish sauce (fish sauce, sugar, lemon, garlic, chili).
                                    4. Put grilled meat into the fish sauce bowl.
                                    5. Serve with fresh vermicelli and herbs.
                                """);
                bunCha.setCookingTimeMinutes(40);
                bunCha.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462792/a6706928-1118-4020-a541-94ca172c63e0.png");
                bunCha.setStatus(RecipeStatus.PUBLISHED);
                bunCha.setRole(MealRole.MAIN_DISH);
                bunCha.setMealType(MealType.LUNCH);
                bunCha.getCategories().add(vietnameseCat);
                bunCha.getCategories().add(grilledCat);
                bunCha.getCategories().add(highProteinCat);

                bunCha.addIngredient(new RecipeIngredient(null, bunCha, porkBelly, 150.0, IngredientUnit.G));
                bunCha.addIngredient(new RecipeIngredient(null, bunCha, groundPork, 80.0, IngredientUnit.G));
                bunCha.addIngredient(new RecipeIngredient(null, bunCha, riceNoodles, 150.0, IngredientUnit.G));
                bunCha.addIngredient(new RecipeIngredient(null, bunCha, fishSauce, 30.0, IngredientUnit.ML));
                bunCha.addIngredient(new RecipeIngredient(null, bunCha, sugar, 20.0, IngredientUnit.G));
                bunCha.addIngredient(new RecipeIngredient(null, bunCha, garlic, 10.0, IngredientUnit.G));
                bunCha.addIngredient(new RecipeIngredient(null, bunCha, lettuce, 80.0, IngredientUnit.G));

                /* ----------------- Recipe 27: Ca Kho To (Caramelized Fish) ----------------- */
                Recipe caKhoTo = new Recipe();
                caKhoTo.setCreatedBy(admin);
                caKhoTo.setTitle("Caramelized Fish in Clay Pot");
                caKhoTo.setDescription("Snakehead fish braised in a clay pot with caramel sauce and black pepper, served with white rice.");
                caKhoTo.setInstructions("""
                                    1. Cut fish into steaks, marinate with fish sauce and pepper.
                                    2. Caramelize sugar to make color water.
                                    3. Sauté shallots and garlic, arrange fish in the clay pot.
                                    4. Pour coconut water and caramel water in, braise on low heat.
                                    5. Braise until sauce thickens, sprinkle with pepper and green onions.
                                """);
                caKhoTo.setCookingTimeMinutes(50);
                caKhoTo.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462827/aaa7d740-9478-436d-bfbe-c606991cfc7e.png");
                caKhoTo.setStatus(RecipeStatus.PUBLISHED);
                caKhoTo.setRole(MealRole.MAIN_DISH);
                caKhoTo.setMealType(MealType.LUNCH);
                caKhoTo.getCategories().add(vietnameseCat);
                caKhoTo.getCategories().add(seafoodCat);
                caKhoTo.getCategories().add(highProteinCat);

                caKhoTo.addIngredient(new RecipeIngredient(null, caKhoTo, catfish, 250.0, IngredientUnit.G));
                caKhoTo.addIngredient(new RecipeIngredient(null, caKhoTo, coconutWater, 100.0, IngredientUnit.ML));
                caKhoTo.addIngredient(new RecipeIngredient(null, caKhoTo, fishSauce, 25.0, IngredientUnit.ML));
                caKhoTo.addIngredient(new RecipeIngredient(null, caKhoTo, sugar, 15.0, IngredientUnit.G));
                caKhoTo.addIngredient(new RecipeIngredient(null, caKhoTo, garlic, 10.0, IngredientUnit.G));
                caKhoTo.addIngredient(new RecipeIngredient(null, caKhoTo, onion, 30.0, IngredientUnit.G));

                /* ----------------- Recipe 28: Tom Nuong Muoi Ot (Grilled Shrimp) ----------------- */
                Recipe tomNuong = new Recipe();
                tomNuong.setCreatedBy(admin);
                tomNuong.setTitle("Grilled Shrimp with Chili Salt");
                tomNuong.setDescription("Fresh tiger prawns grilled with spicy chili salt, crispy shell and sweet meat.");
                tomNuong.setInstructions("""
                                    1. Wash shrimp, keep the shell on.
                                    2. Marinate shrimp with salt, chili powder, minced garlic, oil.
                                    3. Grill shrimp over charcoal or in an oven.
                                    4. Grill until shrimp turns red and shell is slightly charred.
                                    5. Serve with lime pepper salt.
                                """);
                tomNuong.setCookingTimeMinutes(20);
                tomNuong.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462884/272904f1-de54-4003-af8c-7ddd2c419d59.png");
                tomNuong.setStatus(RecipeStatus.PUBLISHED);
                tomNuong.setRole(MealRole.MAIN_DISH);
                tomNuong.setMealType(MealType.DINNER);
                tomNuong.getCategories().add(vietnameseCat);
                tomNuong.getCategories().add(seafoodCat);
                tomNuong.getCategories().add(grilledCat);
                tomNuong.getCategories().add(spicyCat);

                tomNuong.addIngredient(new RecipeIngredient(null, tomNuong, shrimp, 200.0, IngredientUnit.G));
                tomNuong.addIngredient(new RecipeIngredient(null, tomNuong, chili, 15.0, IngredientUnit.G));
                tomNuong.addIngredient(new RecipeIngredient(null, tomNuong, garlic, 10.0, IngredientUnit.G));
                tomNuong.addIngredient(new RecipeIngredient(null, tomNuong, vegetableOil, 15.0, IngredientUnit.ML));

                /* ----------------- Recipe 29: Canh Ga Chien Nuoc Mam (Fried Chicken Wings) ----------------- */
                Recipe canhGaChien = new Recipe();
                canhGaChien.setCreatedBy(admin);
                canhGaChien.setTitle("Fried Chicken Wings with Fish Sauce");
                canhGaChien.setDescription("Crispy fried chicken wings coated in a savory and sweet caramelized fish sauce.");
                canhGaChien.setInstructions("""
                                    1. Marinate chicken wings with salt, pepper, garlic.
                                    2. Fry chicken wings twice for crispiness.
                                    3. Make sauce: simmer fish sauce, sugar, garlic until thickened.
                                    4. Toss chicken wings in the fish sauce mixture.
                                    5. Sprinkle with fried garlic and pepper.
                                """);
                canhGaChien.setCookingTimeMinutes(35);
                canhGaChien.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462891/6f821909-0c23-4f70-b1bf-ea6903965beb.png");
                canhGaChien.setStatus(RecipeStatus.PUBLISHED);
                canhGaChien.setRole(MealRole.MAIN_DISH);
                canhGaChien.setMealType(MealType.DINNER);
                canhGaChien.getCategories().add(vietnameseCat);
                canhGaChien.getCategories().add(friedCat);
                canhGaChien.getCategories().add(highProteinCat);

                canhGaChien.addIngredient(new RecipeIngredient(null, canhGaChien, chicken, 300.0, IngredientUnit.G));
                canhGaChien.addIngredient(new RecipeIngredient(null, canhGaChien, fishSauce, 30.0, IngredientUnit.ML));
                canhGaChien.addIngredient(new RecipeIngredient(null, canhGaChien, sugar, 25.0, IngredientUnit.G));
                canhGaChien.addIngredient(new RecipeIngredient(null, canhGaChien, garlic, 15.0, IngredientUnit.G));
                canhGaChien.addIngredient(new RecipeIngredient(null, canhGaChien, vegetableOil, 100.0, IngredientUnit.ML));

                /* ----------------- Recipe 30: Banh Bao (Steamed Bun) ----------------- */
                Recipe banhBao = new Recipe();
                banhBao.setCreatedBy(admin);
                banhBao.setTitle("Steamed Pork Bun");
                banhBao.setDescription("Soft white bun with pork, quail egg, and wood ear mushroom filling.");
                banhBao.setInstructions("""
                                    1. Make filling: stir-fry minced pork with onions, wood ear mushrooms, season.
                                    2. Knead dough, let it rise.
                                    3. Roll dough, add filling and boiled quail eggs.
                                    4. Wrap buns, let rest for 15 minutes.
                                    5. Steam buns for 15-20 minutes until cooked.
                                """);
                banhBao.setCookingTimeMinutes(60);
                banhBao.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767462991/2ef771fc-5bc7-4d25-bbef-8abd03ae2cdb.png");
                banhBao.setStatus(RecipeStatus.PUBLISHED);
                banhBao.setRole(MealRole.MAIN_DISH);
                banhBao.setMealType(MealType.SNACK);
                banhBao.getCategories().add(vietnameseCat);
                banhBao.getCategories().add(asianCat);

                banhBao.addIngredient(new RecipeIngredient(null, banhBao, groundPork, 100.0, IngredientUnit.G));
                banhBao.addIngredient(new RecipeIngredient(null, banhBao, egg, 2.0, IngredientUnit.EGG_PIECE));
                banhBao.addIngredient(new RecipeIngredient(null, banhBao, woodEarMushroom, 15.0, IngredientUnit.G));
                banhBao.addIngredient(new RecipeIngredient(null, banhBao, onion, 30.0, IngredientUnit.G));
                banhBao.addIngredient(new RecipeIngredient(null, banhBao, fishSauce, 10.0, IngredientUnit.ML));

                /* ----------------- Recipe 31: Cha Ca La Vong (Turmeric Fish) ----------------- */
                Recipe chaCa = new Recipe();
                chaCa.setCreatedBy(admin);
                chaCa.setTitle("La Vong Turmeric Fish");
                chaCa.setDescription("Hanoi specialty with turmeric-fried fish, served with vermicelli and dill.");
                chaCa.setInstructions("""
                                    1. Marinate fish with turmeric, fermented rice, shrimp paste, oil.
                                    2. Fry fish in oil until golden.
                                    3. Add green onions and dill (using cilantro substitute) to the pan.
                                    4. Stir quickly, turn off heat.
                                    5. Serve with vermicelli, roasted peanuts, and shrimp paste.
                                """);
                chaCa.setCookingTimeMinutes(30);
                chaCa.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767463083/ceea9268-fae4-4b43-9de6-41a7412cd15b.png");
                chaCa.setStatus(RecipeStatus.PUBLISHED);
                chaCa.setRole(MealRole.MAIN_DISH);
                chaCa.setMealType(MealType.DINNER);
                chaCa.getCategories().add(vietnameseCat);
                chaCa.getCategories().add(seafoodCat);
                chaCa.getCategories().add(friedCat);

                chaCa.addIngredient(new RecipeIngredient(null, chaCa, catfish, 250.0, IngredientUnit.G));
                chaCa.addIngredient(new RecipeIngredient(null, chaCa, ginger, 10.0, IngredientUnit.G)); // Substitute for turmeric
                chaCa.addIngredient(new RecipeIngredient(null, chaCa, riceNoodles, 150.0, IngredientUnit.G));
                chaCa.addIngredient(new RecipeIngredient(null, chaCa, peanuts, 20.0, IngredientUnit.G));
                chaCa.addIngredient(new RecipeIngredient(null, chaCa, greenOnion, 30.0, IngredientUnit.G));
                chaCa.addIngredient(new RecipeIngredient(null, chaCa, vegetableOil, 50.0, IngredientUnit.ML));

                /* ----------------- Recipe 32: Bun Rieu Cua (Crab Noodle Soup) ----------------- */
                Recipe bunRieu = new Recipe();
                bunRieu.setCreatedBy(admin);
                bunRieu.setTitle("Crab Noodle Soup (Bun Rieu Cua)");
                bunRieu.setDescription("Vermicelli with sour broth from tomatoes and rich field crab paste.");
                bunRieu.setInstructions("""
                                    1. Grind crab with water, filter to get crab liquid.
                                    2. Boil crab liquid, stir gently for crab paste to float.
                                    3. Sauté tomatoes, add to the broth pot.
                                    4. Season with shrimp paste, fish sauce, salt to taste.
                                    5. Blanch vermicelli, pour broth, arrange crab paste, tofu, vegetables.
                                """);
                bunRieu.setCookingTimeMinutes(50);
                bunRieu.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767463132/47869d68-30d6-462d-9a77-9a37b65e5db0.png");
                bunRieu.setStatus(RecipeStatus.PUBLISHED);
                bunRieu.setRole(MealRole.MAIN_DISH);
                bunRieu.setMealType(MealType.LUNCH);
                bunRieu.getCategories().add(vietnameseCat);
                bunRieu.getCategories().add(noodlesCat);
                bunRieu.getCategories().add(seafoodCat);
                bunRieu.getCategories().add(soupCat);

                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, shrimp, 100.0, IngredientUnit.G)); // Substitute for crab
                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, riceNoodles, 150.0, IngredientUnit.G));
                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, tomato, 150.0, IngredientUnit.G));
                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, tofu, 80.0, IngredientUnit.G));
                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, shrimpPaste, 10.0, IngredientUnit.G));
                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, fishSauce, 15.0, IngredientUnit.ML));
                bunRieu.addIngredient(new RecipeIngredient(null, bunRieu, beanSprouts, 50.0, IngredientUnit.G));

                /* ----------------- Recipe 33: Thit Nuong BBQ (Grilled Pork) ----------------- */
                Recipe thitNuong = new Recipe();
                thitNuong.setCreatedBy(admin);
                thitNuong.setTitle("Vietnamese BBQ Grilled Pork");
                thitNuong.setDescription("Pork marinated with lemongrass and chili, grilled over charcoal, fragrant and flavorful.");
                thitNuong.setInstructions("""
                                    1. Slice meat thinly, marinate with lemongrass, garlic, honey, fish sauce.
                                    2. Marinate for at least 2 hours or overnight.
                                    3. Grill meat over charcoal or on a cast iron skillet.
                                    4. Grill until charred and fragrant.
                                    5. Serve with broken rice or vermicelli.
                                """);
                thitNuong.setCookingTimeMinutes(30);
                thitNuong.setImageUrl("https://res.cloudinary.com/dmfvnmpuq/image/upload/v1767463241/5c51e4c7-b3af-46f4-ba7b-4cb6be207b3d.png");
                thitNuong.setStatus(RecipeStatus.PUBLISHED);
                thitNuong.setRole(MealRole.MAIN_DISH);
                thitNuong.setMealType(MealType.DINNER);
                thitNuong.getCategories().add(vietnameseCat);
                thitNuong.getCategories().add(grilledCat);
                thitNuong.getCategories().add(highProteinCat);

                thitNuong.addIngredient(new RecipeIngredient(null, thitNuong, porkChop, 200.0, IngredientUnit.G));
                thitNuong.addIngredient(new RecipeIngredient(null, thitNuong, lemongrass, 30.0, IngredientUnit.G));
                thitNuong.addIngredient(new RecipeIngredient(null, thitNuong, garlic, 10.0, IngredientUnit.G));
                thitNuong.addIngredient(new RecipeIngredient(null, thitNuong, honey, 15.0, IngredientUnit.G));
                thitNuong.addIngredient(new RecipeIngredient(null, thitNuong, fishSauce, 20.0, IngredientUnit.ML));

                /* ----------------- CẬP NHẬT Save all (TỔNG 33 MÓN) ----------------- */
                // Calculate calories for each recipe based on its ingredients and ingredient
                // nutritions
                List<Recipe> recipesToSave = List.of(
                                chickenRice, broccoliStirFry, chickenSalad, bolognese, roastedVegs,
                                phoBo, gaXaoSaOt, rauMuongXaoToi, dauHuSotCa, nemRan,
                                comChien, thitKho, boLucLac, canhChua, goiCuon,
                                bunBoHue, comTam, supGaNgo, banhFlan,
                                banhMi, xoiGa, cheBuoi,
                                miQuang, huTieu, bunCha, caKhoTo, tomNuong,
                                canhGaChien, banhBao, chaCa, bunRieu, thitNuong);

                for (Recipe rx : recipesToSave) {
                        try {
                                BigDecimal cal = CalculateCalories.computeRecipeCalories(rx,
                                                ingredientNutritionRepository);
                                rx.setCalories(cal);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }

                recipeRepository.saveAll(recipesToSave);

                System.out.println("✅ Recipe data initialized successfully!");
        }

}
