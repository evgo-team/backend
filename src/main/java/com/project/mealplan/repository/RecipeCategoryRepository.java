package com.project.mealplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.mealplan.entity.RecipeCategory;

public interface RecipeCategoryRepository extends JpaRepository<RecipeCategory, Long> {
    Optional<RecipeCategory> findByNameIgnoreCase(String name);
    boolean existsByName(String name);

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END
        FROM recipe_category_mapping
        WHERE category_id = :categoryId
        """, nativeQuery = true)
    boolean isCategoryUsedInRecipes(@Param("categoryId") Long categoryId);
}
