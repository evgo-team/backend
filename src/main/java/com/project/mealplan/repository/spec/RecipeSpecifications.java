package com.project.mealplan.repository.spec;

import jakarta.persistence.criteria.Expression;

import org.springframework.data.jpa.domain.Specification;

import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.entity.Recipe;

import java.math.BigDecimal;
import java.util.Locale;

public final class RecipeSpecifications {

    private RecipeSpecifications() {}

    public static Specification<Recipe> isPublicOnlyForUser(boolean isAdmin) {
        if (isAdmin) return (root, query, cb) -> cb.conjunction(); 
        return (root, query, cb) -> cb.equal(root.get("status"), RecipeStatus.PUBLISHED);
    }

    public static Specification<Recipe> hasStatus(RecipeStatus status) {
        return (root, query, cb) -> (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Recipe> hasCategory(String category) {
        return (root, query, cb) -> category == null || category.isBlank()
                ? null
                : cb.like(cb.lower(root.get("category")), "%" + category.trim().toLowerCase(Locale.ROOT) + "%");
    }

    // title OR description, case-insensitive
    public static Specification<Recipe> keywordLike(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String k = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
            Expression<String> title = cb.lower(root.get("title"));
            Expression<String> description = cb.lower(root.get("description"));
            return cb.or(cb.like(title, k), cb.like(description, k));
        };
    }

    public static Specification<Recipe> caloriesBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            // Use stored calories field on recipe for filtering
            if (min != null && max != null) {
                return cb.between(root.get("calories"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("calories"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("calories"), max);
            }
        };
    }

}
