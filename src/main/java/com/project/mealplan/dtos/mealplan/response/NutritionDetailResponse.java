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
public class NutritionDetailResponse {
    private BigDecimal protein;
    private BigDecimal carbs;
    private BigDecimal fat;
}
