package com.project.mealplan.dtos.shoppinglist.response;

import com.project.mealplan.common.enums.IngredientUnit;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private Double quantity;
    private IngredientUnit unit;
    private Boolean isChecked;
}
