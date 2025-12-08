package com.project.mealplan.dtos.recipe.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.project.mealplan.common.enums.RecipeStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeStatus {
    
    @NotNull
    private RecipeStatus status;
}