package com.project.mealplan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.Ingredient;

import java.util.List;
import java.util.Optional;
import org.springframework.lang.Nullable;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long>, JpaSpecificationExecutor<Ingredient> {
        boolean existsByNameIgnoreCase(String name);

        @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) = LOWER(:name)")
        Optional<Ingredient> findByNameIgnoreCase(@Param("name") String name);

        @Query("SELECT i FROM Ingredient i WHERE LOWER(i.name) = LOWER(:name) AND i.id != :id")
        Optional<Ingredient> findByNameIgnoreCaseAndIdNot(@Param("name") String name, @Param("id") Long id);

        @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Ingredient i WHERE LOWER(i.name) = LOWER(:name) AND i.id != :id")
        boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);

        @EntityGraph(attributePaths = { "nutritions", "nutritions.nutritionType" })
        Optional<Ingredient> findWithNutritionsById(Long id);

        @Override
        @EntityGraph(value = "Ingredient.withNutritions")
        Page<Ingredient> findAll(@Nullable Specification<Ingredient> spec, Pageable pageable);

        List<Ingredient> findTop20ByNameContainingIgnoreCase(String name);
}
