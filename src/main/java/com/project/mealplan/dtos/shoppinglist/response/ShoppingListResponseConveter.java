package com.project.mealplan.dtos.shoppinglist.response;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.project.mealplan.entity.ShoppingList;

@Component
public class ShoppingListResponseConveter {

    private final ShoppingListItemResponseConverter itemConverter = new ShoppingListItemResponseConverter();

    public ShoppingListResponse convert(ShoppingList shoppingList) {
        List<ShoppingListItemResponse> items = shoppingList.getItems().stream()
                .map(itemConverter::convert)
                .toList();

        return ShoppingListResponse.builder()
                .id(shoppingList.getId())
                .items(items)
                .createdAt(shoppingList.getCreatedAt())
                .updatedAt(shoppingList.getUpdatedAt())
                .build();
    }

    public List<ShoppingListResponse> convert(List<ShoppingList> shoppingLists) {
        return shoppingLists.stream().map(this::convert).toList();
    }

    public Optional<ShoppingListResponse> convert(Optional<ShoppingList> shoppingList) {
        return shoppingList.map(this::convert);
    }
}