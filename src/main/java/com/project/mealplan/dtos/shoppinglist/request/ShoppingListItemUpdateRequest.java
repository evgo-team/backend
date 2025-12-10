package com.project.mealplan.dtos.shoppinglist.request;

import com.project.mealplan.common.enums.IngredientUnit;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemUpdateRequest {
    private Double quantity;
    private IngredientUnit unit;
    private Boolean isChecked;
}
