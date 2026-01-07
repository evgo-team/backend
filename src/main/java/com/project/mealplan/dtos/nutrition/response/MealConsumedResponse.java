package com.project.mealplan.dtos.nutrition.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for meal consumption log confirmation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealConsumedResponse {

    private Long mealSlotId;
    private Long recipeId;
    private String recipeName;
    private Boolean consumed;
    private LocalDateTime consumedAt;
    private String message;
}
