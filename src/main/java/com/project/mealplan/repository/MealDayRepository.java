package com.project.mealplan.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.MealDay;

@Repository
public interface MealDayRepository extends JpaRepository<MealDay, Long> {

    Optional<MealDay> findByMealPlan_User_UserIdAndDate(Long userId, LocalDate date);
}
