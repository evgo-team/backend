package com.project.mealplan.dtos.mealplan.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMealPlanResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekEndDate;

    private List<MealDayResponse> days;
}
