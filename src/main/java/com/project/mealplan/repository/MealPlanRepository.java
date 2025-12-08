package com.project.mealplan.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.MealPlan;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    Optional<MealPlan> findByUser_UserIdAndStartDate(Long userId, LocalDate startDate);
}
