package com.project.mealplan.service.impl;

import com.project.mealplan.dtos.recipe.response.RecipeShortResponse;
import com.project.mealplan.entity.Recipe;
import com.project.mealplan.entity.User;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.repository.RecipeRepository;
import com.project.mealplan.repository.UserRepository;
import com.project.mealplan.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
        private final UserRepository userRepository;
        private final RecipeRepository recipeRepository;

        @Override
        @Transactional(readOnly = true)
        public List<RecipeShortResponse> getFavorites(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                if (user.getFavorites() == null)
                        return List.of();

                return user.getFavorites().stream()
                                .map(r -> new RecipeShortResponse(
                                                r.getRecipeId(),
                                                r.getTitle(),
                                                r.getImageUrl(),
                                                r.getStatus(),
                                                r.getCategories().stream()
                                                                .map(c -> c.getName())
                                                                .collect(Collectors.toSet()),
                                                r.getCookingTimeMinutes(),
                                                r.getCalories()))
                                .collect(Collectors.toList());

        }

        @Override
        @Transactional
        public RecipeShortResponse addFavorite(Long userId, Long recipeId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                Recipe recipe = recipeRepository.findById(recipeId)
                                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

                if (user.getFavorites() == null) {
                        user.setFavorites(new java.util.HashSet<>());
                }

                boolean already = user.getFavorites().stream()
                                .anyMatch(r -> Objects.equals(r.getRecipeId(), recipe.getRecipeId()));

                if (already) {
                        throw new AppException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }

                user.getFavorites().add(recipe);
                userRepository.saveAndFlush(user);

                return new RecipeShortResponse(
                                recipe.getRecipeId(),
                                recipe.getTitle(),
                                recipe.getImageUrl(),
                                recipe.getStatus(),
                                recipe.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet()),
                                recipe.getCookingTimeMinutes(),
                                recipe.getCalories());
        }

        @Override
        @Transactional
        public RecipeShortResponse removeFavorite(Long userId, Long recipeId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                if (user.getFavorites() == null || user.getFavorites().isEmpty()) {
                        throw new AppException(ErrorCode.FAVORITE_NOT_FOUND);
                }

                Recipe found = null;
                for (Recipe r : user.getFavorites()) {
                        if (Objects.equals(r.getRecipeId(), recipeId)) {
                                found = r;
                                break;
                        }
                }

                if (found == null) {
                        throw new AppException(ErrorCode.FAVORITE_NOT_FOUND);
                }

                boolean removed = user.getFavorites().removeIf(r -> Objects.equals(r.getRecipeId(), recipeId));
                if (removed) {
                        userRepository.saveAndFlush(user);
                }

                return new RecipeShortResponse(
                                found.getRecipeId(),
                                found.getTitle(),
                                found.getImageUrl(),
                                found.getStatus(),
                                found.getCategories().stream().map(c -> c.getName()).collect(Collectors.toSet()),
                                found.getCookingTimeMinutes(),
                                found.getCalories());
        }
}