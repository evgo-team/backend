package com.project.mealplan.service;

import com.project.mealplan.dtos.shoppinglist.request.GenerateShoppingListRequest;
import com.project.mealplan.dtos.shoppinglist.request.ShoppingListItemUpdateRequest;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListItemResponse;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListResponse;

public interface ShoppingListService {
    /**
     * Generate shopping list from a meal plan. Items are merged into user's single
     * shopping list.
     */
    ShoppingListResponse generateShoppingList(Long userId, GenerateShoppingListRequest request);

    /**
     * Get the user's shopping list
     */
    ShoppingListResponse getShoppingList(Long userId);

    /**
     * Clear all items from user's shopping list
     */
    void clearShoppingList(Long userId);

    /**
     * Update a shopping list item. If isChecked becomes true, add to pantry.
     */
    ShoppingListItemResponse updateShoppingListItem(Long userId, Long itemId, ShoppingListItemUpdateRequest request);

    /**
     * Delete a shopping list item
     */
    void deleteShoppingListItem(Long userId, Long itemId);

    /**
     * Bulk check items (mark as purchased and move to pantry)
     */
    void bulkCheckItems(Long userId, java.util.List<Long> itemIds);

    /**
     * Bulk delete items
     */
    void bulkDeleteItems(Long userId, java.util.List<Long> itemIds);

    /**
     * Add a single item to user's shopping list
     */
    ShoppingListItemResponse addItem(Long userId, Long ingredientId, Double quantity,
            com.project.mealplan.common.enums.IngredientUnit unit);
}
