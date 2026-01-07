package com.project.mealplan.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.FoodLog;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {

    List<FoodLog> findByUser_UserIdAndConsumeDateBetweenOrderByConsumeDateAsc(Long userId, LocalDateTime startDate,
            LocalDateTime endDate);

    void deleteByIdAndUser_UserId(Long id, Long userId);

    boolean existsByIdAndUser_UserId(Long id, Long userId);

    // Methods for meal slot sync
    Optional<FoodLog> findByMealSlotIdAndUser_UserId(Long mealSlotId, Long userId);

    void deleteByMealSlotId(Long mealSlotId);
}
