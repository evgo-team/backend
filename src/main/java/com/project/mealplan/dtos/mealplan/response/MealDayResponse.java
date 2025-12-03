package com.project.mealplan.dtos.mealplan.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.mealplan.common.enums.MealType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealDayResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Map<MealType, List<MealSlotResponse>> meals;

    private NutritionSummaryResponse nutritionSummary;
}
