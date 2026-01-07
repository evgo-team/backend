package com.project.mealplan.dtos.mealplan.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMealSlotRecipeRequest {
    @NotNull(message = "Recipe ID cannot be null")
    private Long recipeId;
}
