package com.project.mealplan.repository.spec;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;

import org.springframework.data.jpa.domain.Specification;

import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.entity.Recipe;
import com.project.mealplan.security.CurrentUser;

import java.math.BigDecimal;
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

}
