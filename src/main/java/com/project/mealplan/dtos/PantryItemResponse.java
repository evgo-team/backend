package com.project.mealplan.dtos;

import java.time.LocalDate;

import com.project.mealplan.common.enums.IngredientUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
    private IngredientUnit unit;
    private LocalDate expiresAt;
}
