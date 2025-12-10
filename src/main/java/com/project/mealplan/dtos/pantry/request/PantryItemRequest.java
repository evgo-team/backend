package com.project.mealplan.dtos.pantry.request;

import java.time.LocalDate;

import com.project.mealplan.common.enums.IngredientUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemRequest {
    private Long ingredientId;
    private Double quantity;
    private IngredientUnit unit;
    private LocalDate expiresAt;
}
