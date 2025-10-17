package com.project.mealplan.repository.spec;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.IngredientNutrition;
import com.project.mealplan.entity.NutritionType;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public final class IngredientSpecifications {

    private IngredientSpecifications() {
    }

    // --- Lọc theo type ---
    public static Specification<Ingredient> hasTypeIgnoreCase(String type) {
        return (root, query, cb) -> {
            if (type == null || type.isBlank())
                return cb.conjunction();
            return cb.equal(cb.lower(root.get("type")), type.toLowerCase());
        };
    }

    // --- Lọc theo keyword (name hoặc description) ---
    public static Specification<Ingredient> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank())
                return cb.conjunction();
            String like = "%" + keyword.toLowerCase().trim() + "%";
            Expression<String> nameExpr = cb.lower(root.get("name"));
            // Nếu bảng có cột description thì bật dòng dưới:
            // Expression<String> descExpr = cb.lower(root.get("description"));
            return cb.like(nameExpr, like);
            // Có description thì:
            // return cb.or(cb.like(nameExpr, like), cb.like(descExpr, like));
        };
    }

    // --- Lọc theo khoảng calories ---
    public static Specification<Ingredient> caloriesBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null)
                return cb.conjunction();

            // Subquery để lấy ingredient có nutritionType.name = 'Calories'
            Subquery<Long> sub = query.subquery(Long.class);
            Root<IngredientNutrition> in = sub.from(IngredientNutrition.class);
            Join<IngredientNutrition, NutritionType> nt = in.join("nutritionType", JoinType.INNER);

            Predicate fkMatch = cb.equal(in.get("ingredient").get("id"), root.get("id"));
            Predicate isCalories = cb.equal(cb.lower(nt.get("name")), "calories");

            Predicate amountPred = cb.conjunction();
            if (min != null)
                amountPred = cb.and(amountPred, cb.greaterThanOrEqualTo(in.get("amountPer100g"), min));
            if (max != null)
                amountPred = cb.and(amountPred, cb.lessThanOrEqualTo(in.get("amountPer100g"), max));

            sub.select(in.get("ingredient").get("id"))
                    .where(cb.and(fkMatch, isCalories, amountPred));

            return cb.exists(sub);
        };
    }
}