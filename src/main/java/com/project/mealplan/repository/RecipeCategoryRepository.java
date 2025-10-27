package com.project.mealplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.mealplan.entity.RecipeCategory;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Long> {
    Optional<RecipeCategory> findByNameIgnoreCase(String name);
}

