package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.IngredientNutrition;

@Repository
public interface IngredientNutritionRepository extends JpaRepository<IngredientNutrition, Long> {
        
}