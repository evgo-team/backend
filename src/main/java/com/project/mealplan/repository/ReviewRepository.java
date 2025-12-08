package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.mealplan.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	
}
