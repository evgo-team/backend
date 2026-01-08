package com.project.mealplan.dtos.shoppinglist.request;

import com.project.mealplan.common.enums.IngredientUnit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddShoppingListItemRequest {
    @NotNull(message = "Ingredient ID is required")
    private Long ingredientId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Double quantity;

    @NotNull(message = "Unit is required")
    private IngredientUnit unit;
}
