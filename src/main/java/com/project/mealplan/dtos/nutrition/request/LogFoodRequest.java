package com.project.mealplan.dtos.nutrition.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for logging food from a recipe.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogFoodRequest {

    @NotNull(message = "Recipe ID is required")
    private Long recipeId;

    @NotNull(message = "Consume date is required")
    private LocalDateTime consumeDate;

    /**
     * Quantity/serving multiplier. Default is 1.0.
     */
    @DecimalMin(value = "0.1", message = "Quantity must be at least 0.1")
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ONE;
}
