package com.project.mealplan.dtos.admin;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkDeleteIngredientDto {
    
    @NotEmpty(message = "Ingredient IDs list cannot be empty")
    private List<Long> ids;
}