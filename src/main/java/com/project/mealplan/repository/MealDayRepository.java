package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.MealDay;

@Repository
public interface MealDayRepository extends JpaRepository<MealDay, Long> {
}
