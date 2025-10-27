package com.project.mealplan.repository.spec;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

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

            Subquery<BigDecimal> sub = query.subquery(BigDecimal.class);
            Root<Recipe> r2 = sub.correlate(root);

            Join<?, ?> ri    = r2.join("ingredients", JoinType.LEFT);
            Join<?, ?> ing   = ri.join("ingredient", JoinType.LEFT);
            Join<?, ?> inut  = ing.join("nutritions", JoinType.LEFT);
            Join<?, ?> ntype = inut.join("nutritionType", JoinType.LEFT);

            // name = 'calories' (case-insensitive)
            Predicate isCalories = cb.equal(cb.lower(ntype.get("name")), "calories");

            // kcal/gram = amountPer100g * 0.01
            Expression<BigDecimal> perGram = cb.prod(inut.get("amountPer100g"), new BigDecimal("0.01"));
            // quantity (Double) * perGram
            Expression<BigDecimal> term = cb.prod(cb.toBigDecimal(ri.get("quantity")), perGram);

            sub.select(cb.sum(term));
            sub.where(isCalories);

            if (min != null && max != null) {
                return cb.between(sub, min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(sub, min);
            } else {
                return cb.lessThanOrEqualTo(sub, max);
            }
        };
    }

}
