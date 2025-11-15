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
                                new RecipeCategory(null, "Soup"));
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
                broccoliStirFry.setCreatedBy(admin);
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
                bolognese.setImageUrl("https://images.unsplash.com/photo-1595295333158-e5e3c36b9b6e");
                bolognese.setStatus(RecipeStatus.PUBLISHED);
                bolognese.setRole(MealRole.MAIN_DISH);
                bolognese.setMealType(MealType.DINNER);
                bolognese.getCategories().add(italianCat);

                // Thêm nguyên liệu cho món này
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, groundBeef, 250.0, "g"));
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, tomato, 400.0, "g"));
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, onion, 100.0, "g"));
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, oliveOil, 15.0, "ml"));
                // Giả sử dùng "Rice" thay cho "Pasta" để demo
                bolognese.addIngredient(new RecipeIngredient(null, bolognese, rice, 150.0, "g"));


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
                chickenSalad.setImageUrl("https://images.unsplash.com/photo-1512852939781-939cedf85c41");
                chickenSalad.setStatus(RecipeStatus.PUBLISHED);
                chickenSalad.setRole(MealRole.MAIN_DISH);
                chickenSalad.setMealType(MealType.LUNCH);
                chickenSalad.getCategories().add(healthyCat);
                chickenSalad.getCategories().add(saladCat);
                chickenSalad.getCategories().add(quickCat);

                // Thêm nguyên liệu cho món này
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, chicken, 150.0, "g"));
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, broccoli, 100.0, "g"));
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, carrot, 50.0, "g"));
                chickenSalad.addIngredient(new RecipeIngredient(null, chickenSalad, oliveOil, 10.0, "ml"));

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
                roastedVegs.setImageUrl("https://images.unsplash.com/photo-1518977822511-7f0d7456c3e8");
                roastedVegs.setStatus(RecipeStatus.PUBLISHED);
                roastedVegs.setRole(MealRole.SIDE_DISH);
                roastedVegs.setMealType(MealType.DINNER);
                roastedVegs.getCategories().add(healthyCat);
                roastedVegs.getCategories().add(vegCat); // Món này chay
                roastedVegs.getCategories().add(quickCat);

                // Thêm nguyên liệu cho món này
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, broccoli, 200.0, "g"));
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, carrot, 100.0, "g"));
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, onion, 50.0, "g"));
                roastedVegs.addIngredient(new RecipeIngredient(null, roastedVegs, oliveOil, 15.0, "ml"));

                /* ----------------- Recipe 7: Pho Bo (Pho) ----------------- */
                Recipe phoBo = new Recipe();
                phoBo.setCreatedBy(admin);
                phoBo.setTitle("Phở Bò Hà Nội");
                phoBo.setDescription("Món phở bò truyền thống với nước dùng trong, ngọt thanh.");
                phoBo.setInstructions("""
                                    1. Rửa sạch bắp bò. Nướng gừng và hành tím.
                                    2. Cho xương bò, bắp bò, gừng, hành nướng, hoa hồi, quế vào nồi nước.
                                    3. Hầm ở lửa nhỏ trong 2-3 giờ. Nêm nếm với muối, nước mắm.
                                    4. Trụng bánh phở, giá đỗ.
                                    5. Thái mỏng bắp bò, xếp vào bát cùng bánh phở, hành lá, ngò rí. Chan nước dùng.
                                """);
                phoBo.setCookingTimeMinutes(180);
                phoBo.setImageUrl("https://images.unsplash.com/photo-1568899283083-34d13c7f66cb");
                phoBo.setStatus(RecipeStatus.PUBLISHED);
                phoBo.setRole(MealRole.MAIN_DISH);
                phoBo.setMealType(MealType.BREAKFAST);
                phoBo.getCategories().add(vietnameseCat);
                phoBo.getCategories().add(asianCat);

                phoBo.addIngredient(new RecipeIngredient(null, phoBo, beefShank, 200.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, riceNoodles, 150.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, onion, 50.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, ginger, 10.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, starAnise, 2.0, "unit"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, cinnamonStick, 1.0, "stick"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, greenOnion, 10.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, cilantro, 5.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, beanSprouts, 30.0, "g"));
                phoBo.addIngredient(new RecipeIngredient(null, phoBo, fishSauce, 15.0, "ml"));

                /*
                 * ----------------- Recipe 8: Ga Xao Sa Ot (Lemongrass Chicken)
                 * -----------------
                 */
                Recipe gaXaoSaOt = new Recipe();
                gaXaoSaOt.setCreatedBy(admin);
                gaXaoSaOt.setTitle("Gà xào sả ớt");
                gaXaoSaOt.setDescription("Thịt gà mềm thơm, cay nồng vị sả ớt, đậm đà đưa cơm.");
                gaXaoSaOt.setInstructions("""
                                    1. Thái mỏng ức gà. Băm nhỏ sả, ớt, tỏi.
                                    2. Phi thơm tỏi, sả, ớt trong chảo dầu.
                                    3. Cho thịt gà vào xào săn.
                                    4. Nêm nếm với nước mắm, đường, salt. Đảo đều đến khi gà chín.
                                """);
                gaXaoSaOt.setCookingTimeMinutes(20);
                gaXaoSaOt.setImageUrl("https://images.unsplash.com/photo-1616383686121-b1e4c7e6c4f0"); // Ảnh minh họa
                gaXaoSaOt.setStatus(RecipeStatus.PUBLISHED);
                gaXaoSaOt.setRole(MealRole.MAIN_DISH);
                gaXaoSaOt.setMealType(MealType.DINNER);
                gaXaoSaOt.getCategories().add(vietnameseCat);
                gaXaoSaOt.getCategories().add(quickCat);

                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, chicken, 200.0, "g"));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, lemongrass, 30.0, "g"));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, chili, 10.0, "g"));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, garlic, 5.0, "g"));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, fishSauce, 15.0, "ml"));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, sugar, 5.0, "g"));
                gaXaoSaOt.addIngredient(new RecipeIngredient(null, gaXaoSaOt, vegetableOil, 10.0, "ml"));

                /*
                 * ----------------- Recipe 9: Rau Muong Xao Toi (Stir-fried Water Spinach)
                 * -----------------
                 */
                Recipe rauMuongXaoToi = new Recipe();
                rauMuongXaoToi.setCreatedBy(admin);
                rauMuongXaoToi.setTitle("Rau muống xào tỏi");
                rauMuongXaoToi.setDescription("Món rau quốc dân, xanh giòn, thơm nức mùi tỏi.");
                rauMuongXaoToi.setInstructions("""
                                    1. Nhặt và rửa sạch rau muống. Đập dập tỏi.
                                    2. Phi thơm tỏi với dầu.
                                    3. Vặn lửa lớn, cho rau muống vào đảo nhanh tay.
                                    4. Nêm nếm với nước mắm, salt, đường. Tắt bếp.
                                """);
                rauMuongXaoToi.setCookingTimeMinutes(10);
                rauMuongXaoToi.setImageUrl("https://images.unsplash.com/photo-1628033678857-01765c9b68c9"); // Ảnh minh
                                                                                                            // họa
                rauMuongXaoToi.setStatus(RecipeStatus.PUBLISHED);
                rauMuongXaoToi.setRole(MealRole.SIDE_DISH);
                rauMuongXaoToi.setMealType(MealType.LUNCH);
                rauMuongXaoToi.getCategories().add(vietnameseCat);
                rauMuongXaoToi.getCategories().add(quickCat);
                rauMuongXaoToi.getCategories().add(vegCat);

                rauMuongXaoToi.addIngredient(new RecipeIngredient(null, rauMuongXaoToi, waterSpinach, 300.0, "g"));
                rauMuongXaoToi.addIngredient(new RecipeIngredient(null, rauMuongXaoToi, garlic, 15.0, "g"));
                rauMuongXaoToi.addIngredient(new RecipeIngredient(null, rauMuongXaoToi, fishSauce, 10.0, "ml"));
                rauMuongXaoToi.addIngredient(new RecipeIngredient(null, rauMuongXaoToi, vegetableOil, 15.0, "ml"));

                /*
                 * ----------------- Recipe 10: Dau Hu Sot Ca (Tofu in Tomato Sauce)
                 * -----------------
                 */
                Recipe dauHuSotCa = new Recipe();
                dauHuSotCa.setCreatedBy(admin);
                dauHuSotCa.setTitle("Đậu hũ sốt cà chua");
                dauHuSotCa.setDescription("Đậu hũ mềm béo trong nước sốt cà chua đậm đà, ăn cùng cơm trắng.");
                dauHuSotCa.setInstructions("""
                                    1. Cắt đậu hũ thành miếng vừa ăn, chiên vàng đều.
                                    2. Băm nhỏ cà chua, phi thơm hành/tỏi.
                                    3. Cho cà chua vào xào nhuyễn, thêm nước, nêm nếm mắm, muối, đường.
                                    4. Cho đậu hũ đã chiên vào, om lửa nhỏ 10 phút.
                                    5. Rắc hành lá, ngò rí và tắt bếp.
                                """);
                dauHuSotCa.setCookingTimeMinutes(25);
                dauHuSotCa.setImageUrl("https://images.unsplash.com/photo-1588143224744-3684e5f76e76"); // Ảnh minh họa
                dauHuSotCa.setStatus(RecipeStatus.PUBLISHED);
                dauHuSotCa.setRole(MealRole.MAIN_DISH);
                dauHuSotCa.setMealType(MealType.DINNER);
                dauHuSotCa.getCategories().add(vietnameseCat);
                dauHuSotCa.getCategories().add(vegCat);

                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, tofu, 200.0, "g"));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, tomato, 150.0, "g"));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, greenOnion, 10.0, "g"));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, cilantro, 5.0, "g"));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, fishSauce, 10.0, "ml"));
                dauHuSotCa.addIngredient(new RecipeIngredient(null, dauHuSotCa, vegetableOil, 20.0, "ml"));

                /*
                 * ----------------- Recipe 11: Nem Ran (Fried Spring Rolls) -----------------
                 */
                Recipe nemRan = new Recipe();
                nemRan.setCreatedBy(admin);
                nemRan.setTitle("Nem rán (Chả giò)");
                nemRan.setDescription("Vỏ giòn rụm, nhân thịt và rau củ thơm lừng, chấm nước mắm chua ngọt.");
                nemRan.setInstructions("""
                                    1. Băm nhỏ thịt heo, nấm mèo, cà rốt. Trộn đều với trứng, giá đỗ, miến (nếu có).
                                    2. Nêm nếm với nước mắm, salt, đường.
                                    3. Trải bánh tráng, cho nhân vào và cuốn tròn.
                                    4. Chiên ngập dầu ở lửa vừa đến khi vàng giòn.
                                """);
                nemRan.setCookingTimeMinutes(40);
                nemRan.setImageUrl("https://images.unsplash.com/photo-1534939223126-b8f04f4a56d1"); // Ảnh minh họa
                nemRan.setStatus(RecipeStatus.PUBLISHED);
                nemRan.setRole(MealRole.MAIN_DISH); // Có thể là appetizer
                nemRan.setMealType(MealType.LUNCH);
                nemRan.getCategories().add(vietnameseCat);
                nemRan.getCategories().add(appetizerCat);

                nemRan.addIngredient(new RecipeIngredient(null, nemRan, groundPork, 200.0, "g"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, ricePaper, 10.0, "sheet"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, egg, 1.0, "unit"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, carrot, 50.0, "g"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, woodEarMushroom, 20.0, "g"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, beanSprouts, 30.0, "g"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, fishSauce, 10.0, "ml"));
                nemRan.addIngredient(new RecipeIngredient(null, nemRan, vegetableOil, 100.0, "ml"));

                Recipe comChien = new Recipe();
                comChien.setTitle("Cơm chiên Dương Châu");
                comChien.setDescription(
                                "Cơm chiên tơi xốp, vàng ươm, kết hợp với trứng, rau củ và lạp xưởng (dùng tạm thịt heo).");
                comChien.setInstructions("""
                                    1. Cơm trắng để nguội. Đánh tan trứng.
                                    2. Phi thơm tỏi/hành, cho trứng vào đánh tơi.
                                    3. Cho cơm nguội vào rang ở lửa lớn, đảo đều tay.
                                    4. Thêm đậu Hà Lan, cà rốt (đã luộc sơ), và thịt.
                                    5. Nêm nếm với nước tương (soy sauce), salt, đường. Rắc hành lá.
                                """);
                comChien.setCookingTimeMinutes(15);
                comChien.setImageUrl("https://images.unsplash.com/photo-1599518559032-6013a7c36b85");
                comChien.setStatus(RecipeStatus.PUBLISHED);
                comChien.setRole(MealRole.MAIN_DISH);
                comChien.setMealType(MealType.LUNCH);
                comChien.getCategories().add(vietnameseCat);
                comChien.getCategories().add(asianCat);
                comChien.getCategories().add(quickCat);

                comChien.addIngredient(new RecipeIngredient(null, comChien, rice, 200.0, "g"));
                comChien.addIngredient(new RecipeIngredient(null, comChien, egg, 2.0, "unit"));
                comChien.addIngredient(new RecipeIngredient(null, comChien, carrot, 30.0, "g"));
                comChien.addIngredient(new RecipeIngredient(null, comChien, peas, 30.0, "g"));
                comChien.addIngredient(new RecipeIngredient(null, comChien, groundPork, 50.0, "g")); // Dùng tạm
                comChien.addIngredient(new RecipeIngredient(null, comChien, soySauce, 15.0, "ml"));
                comChien.addIngredient(new RecipeIngredient(null, comChien, greenOnion, 10.0, "g"));

                /*
                 * ----------------- Recipe 13: Thit Kho Trung (Braised Pork with Eggs)
                 * -----------------
                 */
                Recipe thitKho = new Recipe();
                thitKho.setCreatedBy(admin);
                thitKho.setTitle("Thịt kho trứng");
                thitKho.setDescription("Món ăn ngày Tết cổ truyền, thịt ba rọi mềm rục, trứng thấm vị đậm đà.");
                thitKho.setInstructions("""
                                    1. Thái thịt ba rọi thành miếng vuông, ướp với nước mắm, đường, tỏi, hành.
                                    2. Luộc trứng, bóc vỏ.
                                    3. Thắng đường làm nước màu (caramel).
                                    4. Cho thịt vào đảo săn, sau đó cho nước màu, nước dừa tươi vào.
                                    5. Thêm trứng đã luộc. Kho ở lửa nhỏ 1-2 giờ cho đến khi thịt mềm.
                                """);
                thitKho.setCookingTimeMinutes(120);
                thitKho.setImageUrl("https://images.unsplash.com/photo-1568205763539-01c0c60d110d"); // Ảnh minh họa
                thitKho.setStatus(RecipeStatus.PUBLISHED);
                thitKho.setRole(MealRole.MAIN_DISH);
                thitKho.setMealType(MealType.DINNER);
                thitKho.getCategories().add(vietnameseCat);

                thitKho.addIngredient(new RecipeIngredient(null, thitKho, porkBelly, 300.0, "g"));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, egg, 4.0, "unit"));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, coconutWater, 200.0, "ml"));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, fishSauce, 30.0, "ml"));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, sugar, 20.0, "g"));
                thitKho.addIngredient(new RecipeIngredient(null, thitKho, garlic, 10.0, "g"));

                /* ----------------- Recipe 14: Bo Luc Lac (Shaking Beef) ----------------- */
                Recipe boLucLac = new Recipe();
                boLucLac.setCreatedBy(admin);
                boLucLac.setTitle("Bò lúc lắc");
                boLucLac.setDescription("Bò thăn thái hạt lựu, xào lửa lớn với hành tây, ớt chuông, ăn kèm salad.");
                boLucLac.setInstructions("""
                                    1. Thái bò thăn thành khối vuông, ướp với dầu hào, nước tương, tỏi.
                                    2. Thái hành tây, cà chua.
                                    3. Phi tỏi thơm, cho bò vào xào nhanh ở lửa lớn (lúc lắc chảo).
                                    4. Thêm hành tây, đảo nhanh rồi tắt bếp.
                                    5. Dọn ra đĩa salad (rau xà lách, cà chua).
                                """);
                boLucLac.setCookingTimeMinutes(20);
                boLucLac.setImageUrl("https://images.unsplash.com/photo-1629503507663-01a61c73c68b"); // Ảnh minh họa
                boLucLac.setStatus(RecipeStatus.PUBLISHED);
                boLucLac.setRole(MealRole.MAIN_DISH);
                boLucLac.setMealType(MealType.DINNER);
                boLucLac.getCategories().add(vietnameseCat);
                boLucLac.getCategories().add(quickCat);

                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, beefSirloin, 200.0, "g"));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, onion, 50.0, "g"));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, lettuce, 100.0, "g"));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, tomato, 50.0, "g"));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, soySauce, 15.0, "ml"));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, oysterSauce, 10.0, "ml"));
                boLucLac.addIngredient(new RecipeIngredient(null, boLucLac, garlic, 10.0, "g"));

                /*
                 * ----------------- Recipe 15: Canh Chua Ca (Vietnamese Sour Fish Soup)
                 * -----------------
                 */
                Recipe canhChua = new Recipe();
                canhChua.setCreatedBy(admin);
                canhChua.setTitle("Canh chua cá lóc");
                canhChua.setDescription(
                                "Canh cá vị chua thanh từ me, ngọt từ dứa (dùng tạm cà chua), thơm mùi rau nêm.");
                canhChua.setInstructions("""
                                    1. Sơ chế cá, ướp với nước mắm.
                                    2. Nấu sôi nước, cho nước me (tamarind paste) vào.
                                    3. Cho cá vào nấu chín.
                                    4. Thêm cà chua, đậu bắp, giá đỗ.
                                    5. Nêm nếm mắm, muối, đường cho vừa vị chua-mặn-ngọt.
                                    6. Tắt bếp, thêm hành lá, ngò rí, tỏi phi.
                                """);
                canhChua.setCookingTimeMinutes(30);
                canhChua.setImageUrl("https://images.unsplash.com/photo-1628033678857-01765c9b68c9"); // Ảnh minh họa
                canhChua.setStatus(RecipeStatus.PUBLISHED);
                canhChua.setRole(MealRole.SOUP);
                canhChua.setMealType(MealType.DINNER);
                canhChua.getCategories().add(vietnameseCat);
                canhChua.getCategories().add(soupCat);

                canhChua.addIngredient(new RecipeIngredient(null, canhChua, catfish, 200.0, "g"));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, tamarindPaste, 30.0, "g"));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, tomato, 100.0, "g"));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, beanSprouts, 50.0, "g"));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, okra, 50.0, "g"));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, cilantro, 10.0, "g"));
                canhChua.addIngredient(new RecipeIngredient(null, canhChua, fishSauce, 20.0, "ml"));

                /*
                 * ----------------- Recipe 16: Goi Cuon (Fresh Spring Rolls) -----------------
                 */
                Recipe goiCuon = new Recipe();
                goiCuon.setCreatedBy(admin);
                goiCuon.setTitle("Gỏi cuốn tôm thịt");
                goiCuon.setDescription("Món khai vị thanh mát, cuốn bánh tráng với bún, rau sống, tôm, thịt.");
                goiCuon.setInstructions("""
                                    1. Luộc chín tôm và thịt ba rọi. Thái mỏng thịt, chẻ đôi tôm.
                                    2. Trụng sơ bún (rice noodles).
                                    3. Nhúng bánh tráng (rice paper) vào nước cho mềm.
                                    4. Trải bánh tráng, xếp rau xà lách, ngò rí, bún, thịt, tôm lên trên.
                                    5. Cuốn chặt tay. Ăn kèm tương chấm (đậu phộng).
                                """);
                goiCuon.setCookingTimeMinutes(25);
                goiCuon.setImageUrl("https://images.unsplash.com/photo-1512152272829-e3139592d56f");
                goiCuon.setStatus(RecipeStatus.PUBLISHED);
                goiCuon.setRole(MealRole.MAIN_DISH); // Có thể là Appetizer
                goiCuon.setMealType(MealType.SNACK);
                goiCuon.getCategories().add(vietnameseCat);
                goiCuon.getCategories().add(appetizerCat);
                goiCuon.getCategories().add(healthyCat);

                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, shrimp, 100.0, "g"));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, porkBelly, 100.0, "g"));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, ricePaper, 10.0, "sheet"));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, riceNoodles, 100.0, "g"));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, lettuce, 50.0, "g"));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, cilantro, 20.0, "g"));
                goiCuon.addIngredient(new RecipeIngredient(null, goiCuon, peanuts, 30.0, "g")); // Cho nước chấm

                // /* ----------------- Recipe 17: Bun Bo Hue ----------------- */
                Recipe bunBoHue = new Recipe();
                bunBoHue.setCreatedBy(admin);
                bunBoHue.setTitle("Bún bò Huế");
                bunBoHue.setDescription("Nước dùng đậm đà, cay nồng vị sả và mắm ruốc đặc trưng của Huế.");
                bunBoHue.setInstructions(
                                """
                                                    1. Hầm bắp bò, giò heo với sả đập dập.
                                                    2. Phi thơm sả, tỏi, ớt, sau đó thêm mắm ruốc (đã pha loãng) vào xào.
                                                    3. Cho hỗn hợp sả ớt vào nồi nước hầm, nêm nếm mắm, muối, đường.
                                                    4. Trụng bún, xếp thịt, giò heo, rắc hành lá. Chan nước dùng và thêm sa tế (chili oil).
                                                """);
                bunBoHue.setCookingTimeMinutes(150);
                bunBoHue.setImageUrl("https://images.unsplash.com/photo-1568899283083-34d13c7f66cb"); // Dùng lại ảnh
                                                                                                      // Phở
                bunBoHue.setStatus(RecipeStatus.PUBLISHED);
                bunBoHue.setRole(MealRole.MAIN_DISH);
                bunBoHue.setMealType(MealType.BREAKFAST);
                bunBoHue.getCategories().add(vietnameseCat);
                bunBoHue.getCategories().add(soupCat);

                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, thickRiceNoodles, 150.0, "g"));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, beefShank, 100.0, "g"));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, porkHock, 100.0, "g"));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, lemongrass, 50.0, "g"));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, shrimpPaste, 10.0, "g"));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, chiliOil, 5.0, "g"));
                bunBoHue.addIngredient(new RecipeIngredient(null, bunBoHue, fishSauce, 15.0, "ml"));

                // /* ----------------- Recipe 18: Com Tam Suon (Broken Rice) ----------------- */
                Recipe comTam = new Recipe();
                comTam.setCreatedBy(admin);
                comTam.setTitle("Cơm tấm sườn nướng");
                comTam.setDescription("Sườn cốt lết nướng mật ong thơm lừng, ăn kèm cơm tấm và nước mắm chua ngọt.");
                comTam.setInstructions("""
                                    1. Sườn cốt lết ướp với nước mắm, mật ong, tỏi, hành.
                                    2. Nướng sườn trên than hoặc lò nướng cho đến khi xém vàng.
                                    3. Nấu cơm tấm.
                                    4. Pha nước mắm chua ngọt (nước mắm, đường, chanh/giấm, tỏi, ớt).
                                    5. Dọn cơm, sườn, mỡ hành (green onion + oil), và nước mắm.
                                """);
                comTam.setCookingTimeMinutes(45);
                comTam.setImageUrl("https://images.unsplash.com/photo-1596279610119-a1c2de6062a4"); // Ảnh minh họa
                comTam.setStatus(RecipeStatus.PUBLISHED);
                comTam.setRole(MealRole.MAIN_DISH);
                comTam.setMealType(MealType.LUNCH);
                comTam.getCategories().add(vietnameseCat);

                comTam.addIngredient(new RecipeIngredient(null, comTam, porkChop, 150.0, "g"));
                comTam.addIngredient(new RecipeIngredient(null, comTam, brokenRice, 150.0, "g"));
                comTam.addIngredient(new RecipeIngredient(null, comTam, fishSauce, 20.0, "ml"));
                comTam.addIngredient(new RecipeIngredient(null, comTam, honey, 10.0, "g"));
                comTam.addIngredient(new RecipeIngredient(null, comTam, garlic, 5.0, "g"));
                comTam.addIngredient(new RecipeIngredient(null, comTam, greenOnion, 10.0, "g"));

                /*
                 * ----------------- Recipe 19: Sup Ga Ngo Non (Chicken Corn Soup)
                 * -----------------
                 */
                Recipe supGaNgo = new Recipe();
                supGaNgo.setCreatedBy(admin);
                supGaNgo.setTitle("Súp gà ngô non");
                supGaNgo.setDescription("Món súp khai vị thanh ngọt, sánh nhẹ, với gà xé và hạt ngô.");
                supGaNgo.setInstructions("""
                                    1. Luộc ức gà, giữ lại nước dùng. Xé nhỏ thịt gà.
                                    2. Cho ngô ngọt và gà xé vào nước dùng, đun sôi.
                                    3. Nêm nếm với salt, đường.
                                    4. Pha bột bắp (cornstarch) với nước, đổ từ từ vào nồi, khuấy đều cho súp sánh lại.
                                    5. Đánh tan trứng, đổ từ từ vào súp tạo vân.
                                    6. Rắc hành lá, ngò rí và tiêu.
                                """);
                supGaNgo.setCookingTimeMinutes(25);
                supGaNgo.setImageUrl("https://images.unsplash.com/photo-1596797038530-2c05c1d44f33"); // Ảnh minh họa
                supGaNgo.setStatus(RecipeStatus.PUBLISHED);
                supGaNgo.setRole(MealRole.SOUP);
                supGaNgo.setMealType(MealType.SNACK);
                supGaNgo.getCategories().add(soupCat);
                supGaNgo.getCategories().add(appetizerCat);
                supGaNgo.getCategories().add(asianCat);

                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, chicken, 100.0, "g"));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, sweetCorn, 100.0, "g"));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, egg, 1.0, "unit"));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, cornstarch, 15.0, "g"));
                supGaNgo.addIngredient(new RecipeIngredient(null, supGaNgo, cilantro, 5.0, "g"));

                /* ----------------- Recipe 20: Banh Flan (Caramel Custard) ----------------- */
                Recipe banhFlan = new Recipe();
                banhFlan.setCreatedBy(admin);
                banhFlan.setTitle("Bánh flan (Caramel Custard)");
                banhFlan.setDescription("Món tráng miệng mềm mịn, béo ngậy vị trứng sữa, thơm lừng vị caramel.");
                banhFlan.setInstructions("""
                                    1. Thắng đường (sugar) với nước để làm caramel, đổ vào đáy khuôn.
                                    2. Đánh tan trứng (eggs).
                                    3. Hâm nóng sữa tươi (milk) và sữa đặc (condensed milk). Không đun sôi.
                                    4. Từ từ đổ hỗn hợp sữa vào trứng, khuấy đều. Thêm vani.
                                    5. Lọc hỗn hợp qua rây, đổ vào khuôn đã có caramel.
                                    6. Hấp hoặc nướng cách thủy ở 150°C trong 40-50 phút.
                                    7. Để nguội và làm lạnh trước khi ăn.
                                """);
                banhFlan.setCookingTimeMinutes(60);
                banhFlan.setImageUrl("https://images.unsplash.com/photo-1549488344-160b86a8a38b");
                banhFlan.setStatus(RecipeStatus.PUBLISHED);
                banhFlan.setRole(MealRole.DESSERT);
                banhFlan.setMealType(MealType.SNACK);
                banhFlan.getCategories().add(dessertCat);

                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, egg, 4.0, "unit"));
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, milk, 300.0, "ml"));
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, condensedMilk, 100.0, "g"));
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, sugar, 50.0, "g")); // Để làm caramel
                banhFlan.addIngredient(new RecipeIngredient(null, banhFlan, vanillaExtract, 5.0, "ml"));

                /* ----------------- CẬP NHẬT Save all (TỔNG 20 MÓN) ----------------- */
                // Calculate calories for each recipe based on its ingredients and ingredient
                // nutritions
                List<Recipe> recipesToSave = List.of(
                                chickenRice, broccoliStirFry, chickenSalad, bolognese, roastedVegs,
                                phoBo, gaXaoSaOt, rauMuongXaoToi, dauHuSotCa, nemRan,
                                comChien, thitKho, boLucLac, canhChua, goiCuon,
                                bunBoHue, comTam, supGaNgo, banhFlan);
                
                for (Recipe rx : recipesToSave) {
                        try {
                                BigDecimal cal = CalculateCalories.computeRecipeCalories(rx, ingredientNutritionRepository);
                                rx.setCalories(cal);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }

                recipeRepository.saveAll(recipesToSave);

                System.out.println("✅ Recipe data initialized successfully!");
        }

}
