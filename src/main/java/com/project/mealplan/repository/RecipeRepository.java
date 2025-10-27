package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {
    boolean existsByTitleAndStatus(String title, RecipeStatus status);
}

