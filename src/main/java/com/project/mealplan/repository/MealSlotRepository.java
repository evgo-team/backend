package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.MealSlot;

@Repository
public interface MealSlotRepository extends JpaRepository<MealSlot, Long> {
}
