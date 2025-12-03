package com.project.mealplan.dtos.mealplan.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSlotResponse {
    private Long mealSlotId;
    private Long recipeId;
    private String title;
    private BigDecimal calories;
    private Double score;
}
