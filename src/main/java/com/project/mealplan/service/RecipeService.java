package com.project.mealplan.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.dtos.recipe.request.RecipeCreateRequest;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeDto;
import com.project.mealplan.dtos.recipe.response.RecipeResponseDto;
import com.project.mealplan.dtos.recipe.response.RecipeShortResponse;
import com.project.mealplan.security.CurrentUser;
import com.project.mealplan.dtos.recipe.DeleteRecipesDto;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeStatus;

public interface RecipeService {
    public RecipeResponseDto createRecipe(RecipeCreateRequest request, CurrentUser currentUser);

    RecipeResponseDto updateRecipe(Long id, UpdateRecipeDto dto, CurrentUser currentUser);

    RecipeResponseDto getRecipeById(Long id, CurrentUser currentUser);

    Page<RecipeShortResponse> getRecipes(
        CurrentUser currentUser,
        RecipeStatus status,
        String category,
        BigDecimal minCalories,
        BigDecimal maxCalories,
        Integer page,
        Integer size,
        String sortBy,
        String sortDir,
        String keyword
    );
    
    RecipeResponseDto updateRecipeStatus(Long id, UpdateRecipeStatus status, CurrentUser currentUser);

    // Delete a recipe (and its related recipe_ingredient rows) by id. Admin only.
    void deleteRecipe(Long id, CurrentUser currentUser);
    
    // Delete multiple recipes by their ids. Return the number of recipes actually deleted.
    // Non-existent ids are ignored. Operation should be performed in a transaction.
    int deleteRecipesByIds(List<Long> ids, DeleteRecipesDto body);
}
