package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.IngredientNutrition;

@Repository
public interface IngredientNutritionRepository extends JpaRepository<IngredientNutrition, Long> {
	// Find a nutrition entry for a specific ingredient by nutrition type name (case-insensitive)
	java.util.Optional<IngredientNutrition> findFirstByIngredient_IdAndNutritionType_NameIgnoreCase(Long ingredientId, String name);
}