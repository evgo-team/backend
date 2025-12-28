package com.project.mealplan.dtos.mealplan.response;

import java.time.LocalDate;
import java.util.List;

import com.project.mealplan.common.enums.MealType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealSlotListResponse {
    private LocalDate date;
    private MealType mealType;
    private List<MealSlotDetailResponse> slots;
    private NutritionSummaryResponse nutritionSummary;
}
