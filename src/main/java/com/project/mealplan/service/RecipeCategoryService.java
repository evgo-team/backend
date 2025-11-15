package com.project.mealplan.service;

import java.util.List;

import com.project.mealplan.dtos.recipeCategory.request.createCategoryRequest;
import com.project.mealplan.dtos.recipeCategory.request.updateCategoryRequest;
import com.project.mealplan.dtos.recipeCategory.response.RecipeCategoryDto;

public interface RecipeCategoryService {
	List<RecipeCategoryDto> getAllCategories();
	RecipeCategoryDto createCategory(createCategoryRequest request);
	RecipeCategoryDto updateCategory(updateCategoryRequest request);
	void deleteRecipeCategory(Long id);
	
}
