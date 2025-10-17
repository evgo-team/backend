package com.project.mealplan.dtos.admin;

import com.project.mealplan.common.enums.IngredientType;
import lombok.Data;

import java.util.List;

@Data
public class IngredientResponseDto {

        private Long id;
        private String name;
        private IngredientType type;
        private List<NutritionInfo> nutritions;

        @Data
        public static class NutritionInfo {
                private Long nutritionTypeId;
                private String nutritionTypeName;
                private String unit;
                private Double amountPer100g;
        }
}