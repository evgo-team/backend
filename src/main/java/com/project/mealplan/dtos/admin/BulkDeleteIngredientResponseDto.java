package com.project.mealplan.dtos.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkDeleteIngredientResponseDto {

        private int totalRequested;
        private int successfullyDeleted;
        private int failed;
        private List<Long> failedIds;
        private List<String> errors;
}