package com.project.mealplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.MealSlot;

@Repository
public interface MealSlotRepository extends JpaRepository<MealSlot, Long> {

    Optional<MealSlot> findByIdAndMealDay_MealPlan_User_UserId(Long id, Long userId);
}
