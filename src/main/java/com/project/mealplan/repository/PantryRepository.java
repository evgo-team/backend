package com.project.mealplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.Pantry;

@Repository
public interface PantryRepository extends JpaRepository<Pantry, Long> {
    Optional<Pantry> findByUser_UserId(Long userId);
}
