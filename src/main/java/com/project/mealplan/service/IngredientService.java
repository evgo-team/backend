package com.project.mealplan.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.project.mealplan.dtos.admin.BulkDeleteIngredientDto;
import com.project.mealplan.dtos.admin.BulkDeleteIngredientResponseDto;
import com.project.mealplan.dtos.admin.IngredientResponseDto;
import com.project.mealplan.dtos.admin.UpdateIngredientDto;
import com.project.mealplan.dtos.ingredient.request.IngredientCreateRequest;
import com.project.mealplan.dtos.ingredient.response.IngredientListItemResponse;
import com.project.mealplan.dtos.ingredient.response.IngredientResponse;
import com.project.mealplan.entity.NutritionType;

public interface IngredientService {
        IngredientResponse createIngredient(IngredientCreateRequest req);

        IngredientResponseDto updateIngredient(Long id, UpdateIngredientDto updateDto);

        void deleteIngredient(Long id);

        BulkDeleteIngredientResponseDto bulkDeleteIngredients(BulkDeleteIngredientDto bulkDeleteDto);

        IngredientResponse getIngredientDetailById(Long id);

        Page<IngredientListItemResponse> searchIngredients(
                        String type,
                        BigDecimal minCalories,
                        BigDecimal maxCalories,
                        String keyword,
                        int page,
                        int size,
                        String sortBy,
                        String sortDir);

        List<NutritionType> getAllNutritions();
}