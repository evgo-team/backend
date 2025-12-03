package com.project.mealplan.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "meal_days")
public class MealDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "meal_plan_id", nullable = false)
    private MealPlan mealPlan;

    @Column(nullable = false)
    private LocalDate date;

    @OneToMany(mappedBy = "mealDay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealSlot> mealSlots = new ArrayList<>();

    // Helper method
    public void addMealSlot(MealSlot mealSlot) {
        mealSlots.add(mealSlot);
        mealSlot.setMealDay(this);
    }
}
