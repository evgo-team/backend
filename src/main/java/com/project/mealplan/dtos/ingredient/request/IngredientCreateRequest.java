package com.project.mealplan.dtos.ingredient.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

public record IngredientCreateRequest(
    @NotBlank(message = "Tên nguyên liệu không được để trống")
    String name,

    @NotBlank(message = "Loại nguyên liệu không được để trống")
    String type,

    @NotNull(message = "Density không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Density phải lớn hơn 0")
    BigDecimal density,

    @NotEmpty(message = "Phải có ít nhất 1 thông tin dinh dưỡng")
    @Valid
    List<NutritionDto> nutritions
) {
    public record NutritionDto(
        @NotBlank(message = "Tên loại dinh dưỡng không được để trống")
        String nutritionName,

        @NotNull(message = "Giá trị dinh dưỡng không được để trống")
        @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị dinh dưỡng phải lớn hơn 0")
        BigDecimal amountPer100g
    ) {}
}

