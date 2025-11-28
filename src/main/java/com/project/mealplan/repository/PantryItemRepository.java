package com.project.mealplan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.mealplan.entity.PantryItem;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {

}
