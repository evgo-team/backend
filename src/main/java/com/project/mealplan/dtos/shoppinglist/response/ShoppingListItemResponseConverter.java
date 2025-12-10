package com.project.mealplan.dtos.shoppinglist.response;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.project.mealplan.entity.ShoppingListItem;

@Component
public class ShoppingListItemResponseConverter {
    public ShoppingListItemResponse convert(ShoppingListItem item) {
        return ShoppingListItemResponse.builder()
                .id(item.getId())
                .ingredientId(item.getIngredient().getId())
                .ingredientName(item.getIngredient().getName())
                .quantity(item.getQuantity())
                .unit(item.getUnit())
                .isChecked(item.getIsChecked())
                .build();
    }

    public List<ShoppingListItemResponse> convert(List<ShoppingListItem> items) {
        return items.stream().map(this::convert).toList();
    }

    public Optional<ShoppingListItemResponse> convert(Optional<ShoppingListItem> item) {
        return item.map(this::convert);
    }
}
