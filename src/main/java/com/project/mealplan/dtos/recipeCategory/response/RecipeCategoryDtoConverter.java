package com.project.mealplan.dtos.recipeCategory.response;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.project.mealplan.entity.RecipeCategory;

@Component
public class RecipeCategoryDtoConverter {

	public RecipeCategoryDto convert(RecipeCategory category) {
		return new RecipeCategoryDto(
				category.getId(),
				category.getName()
		);
	}

	public List<RecipeCategoryDto> convert(List<RecipeCategory> categories) {
		return categories.stream().map(this::convert).toList();
	}

	public Optional<RecipeCategoryDto> convert(Optional<RecipeCategory> categoryOpt) {
		return categoryOpt.map(this::convert);
	}
}
