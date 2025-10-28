package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.mealplan.entity.RecipeIngredient;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
	
}
