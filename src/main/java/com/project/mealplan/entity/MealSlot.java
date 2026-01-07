package com.project.mealplan.entity;

import java.time.LocalDateTime;

import com.project.mealplan.common.enums.MealType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_slots")
public class MealSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_day_id", nullable = false)
    private MealDay mealDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType type;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column
    private Double quantity;

    /**
     * Indicates whether the user has actually consumed this meal.
     * Only consumed meals count towards nutrition tracking.
     */
    @Column(nullable = false)
    private Boolean consumed = false;

    /**
     * Timestamp when the meal was marked as consumed.
     */
    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;
}
