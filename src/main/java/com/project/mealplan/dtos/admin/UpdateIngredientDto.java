package com.project.mealplan.dtos.admin;

import com.project.mealplan.common.enums.IngredientType;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateIngredientDto {

        @NotBlank(message = "Tên ingredient không được để trống")
        @Size(max = 100, message = "Tên ingredient không được vượt quá 100 ký tự")
        private String name;

        @NotNull(message = "Loại ingredient không được để trống")
        private IngredientType type;

        @Valid
        private List<IngredientNutritionDto> nutritions;

        @Data
        public static class IngredientNutritionDto {

                @NotNull(message = "Nutrition type ID không được để trống")
                private Long nutritionTypeId;

                @NotNull(message = "Lượng dinh dưỡng không được để trống")
                private Double amountPer100g;
        }
}