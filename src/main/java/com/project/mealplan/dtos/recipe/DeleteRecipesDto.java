package com.project.mealplan.dtos.recipe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRecipesDto {
    // list of recipe ids to delete
    private List<Long> ids;
}
