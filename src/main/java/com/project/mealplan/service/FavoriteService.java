package com.project.mealplan.service;

import com.project.mealplan.dtos.recipe.response.RecipeShortResponse;
import java.util.List;

public interface FavoriteService {
    List<RecipeShortResponse> getFavorites(Long userId);

    RecipeShortResponse addFavorite(Long userId, Long recipeId);

    RecipeShortResponse removeFavorite(Long userId, Long recipeId);
}
