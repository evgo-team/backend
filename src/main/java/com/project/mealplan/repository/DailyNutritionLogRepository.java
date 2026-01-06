package com.project.mealplan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.DailyNutritionLog;

@Repository
public interface DailyNutritionLogRepository extends JpaRepository<DailyNutritionLog, Long> {

    Optional<DailyNutritionLog> findByUser_UserIdAndDate(Long userId, LocalDate date);

    List<DailyNutritionLog> findByUser_UserIdAndDateBetweenOrderByDateAsc(
            Long userId, LocalDate startDate, LocalDate endDate);

    boolean existsByUser_UserIdAndDate(Long userId, LocalDate date);
}
