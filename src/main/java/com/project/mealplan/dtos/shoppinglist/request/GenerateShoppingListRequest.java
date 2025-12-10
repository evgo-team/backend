package com.project.mealplan.dtos.shoppinglist.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateShoppingListRequest {
    @NotNull(message = "Meal plan ID is required")
    private Long mealPlanId;
}
