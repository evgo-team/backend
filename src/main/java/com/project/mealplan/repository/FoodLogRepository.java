package com.project.mealplan.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.FoodLog;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {

    List<FoodLog> findByUser_UserIdAndConsumeDateBetweenOrderByConsumeDateAsc(Long userId, LocalDateTime startDate,
            LocalDateTime endDate);

    void deleteByIdAndUser_UserId(Long id, Long userId);

    boolean existsByIdAndUser_UserId(Long id, Long userId);
}
