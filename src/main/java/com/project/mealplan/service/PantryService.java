package com.project.mealplan.service;

import java.util.List;

import com.project.mealplan.dtos.PantryItemRequest;
import com.project.mealplan.dtos.PantryItemResponse;

public interface PantryService {
    List<PantryItemResponse> getPantryItemsByUserId(Long userId);
    PantryItemResponse addPantryItem(Long userId, PantryItemRequest request);
    PantryItemResponse updatePantryItem(Long userId, Long itemId, PantryItemRequest request);
    void deletePantryItem(Long userId, Long itemId);
}
