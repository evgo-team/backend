package com.project.mealplan.repository.spec;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import com.project.mealplan.common.enums.MealType;
import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.entity.Recipe;
import com.project.mealplan.security.CurrentUser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public final class RecipeSpecifications {

    private RecipeSpecifications() {}

    public static Specification<Recipe> isPublicOnlyForUser(CurrentUser currentUser) {
        // ADMIN: Thấy tất cả
        if (currentUser.isAdmin()) {
            return (root, query, cb) -> cb.conjunction();
        }

        // USER: Thấy (status = PUBLISHED) HOẶC (mình là tác giả 'createdBy')
        return (root, query, cb) -> {
            Predicate isPublished = cb.equal(root.get("status"), RecipeStatus.PUBLISHED);
            Predicate isOwner = cb.equal(root.get("createdBy").get("userId"), currentUser.getId()); 
            return cb.or(isPublished, isOwner);
        };
    }

    public static Specification<Recipe> hasStatus(RecipeStatus status) {
        return (root, query, cb) -> (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Recipe> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank()) {
                return null;
            }

            Join<Object, Object> categoryJoin = root.join("categories");

            return cb.like(
                cb.lower(categoryJoin.get("name")),
                "%" + category.trim().toLowerCase(Locale.ROOT) + "%"
            );
        };
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

    public static Specification<Recipe> hasAnyIngredientIds(List<Long> ingredientIds) {
        return (root, query, cb) -> {
            if (ingredientIds == null || ingredientIds.isEmpty()) {
                return null;
            }

            // Vì join sang nhiều dòng nên phải distinct để tránh duplicate recipes
            if (query.getResultType().equals(Recipe.class)) {
                query.distinct(true);
            }

            // JOIN: Recipe -> ingredients -> ingredient
            Join<Object, Object> riJoin = root.join("ingredients"); 
            Join<Object, Object> ingJoin = riJoin.join("ingredient");

            return ingJoin.get("id").in(ingredientIds);
        };
    }

    public static Specification<Recipe> hasMealType(String mealType) {
        return (root, query, cb) -> {
            if (mealType == null || mealType.isBlank())
                return null;
            try {
                MealType mt = MealType.valueOf(mealType.trim().toUpperCase());
                return cb.equal(root.get("mealType"), mt);
            } catch (Exception e) {
                return null;
            }
        };
    }

    public static Specification<Recipe> cookingTimeBetween(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return null;
            if (min != null && max != null) {
                return cb.between(root.get("cookingTimeMinutes"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("cookingTimeMinutes"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("cookingTimeMinutes"), max);
            }
        };
    }

}
