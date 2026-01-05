package com.project.mealplan.dtos.nutrition.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for marking a meal slot as consumed or not consumed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogMealConsumedRequest {

    /**
     * Whether the meal was consumed.
     * If true, marks the meal as eaten.
     * If false, marks the meal as not eaten (undo).
     */
    private Boolean consumed;
}
