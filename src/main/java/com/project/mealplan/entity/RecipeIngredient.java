package com.project.mealplan.entity;

import com.project.mealplan.common.enums.IngredientUnit;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "recipe_ingredient", uniqueConstraints = @UniqueConstraint(columnNames = { "recipe_id",
        "ingredient_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    private Double quantity;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private IngredientUnit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RecipeIngredient))
            return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
