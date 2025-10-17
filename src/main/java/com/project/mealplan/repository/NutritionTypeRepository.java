package com.project.mealplan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.NutritionType;

@Repository
public interface NutritionTypeRepository extends JpaRepository<NutritionType, Long>{	
	Optional<NutritionType> findByNameIgnoreCase(String name);
}
