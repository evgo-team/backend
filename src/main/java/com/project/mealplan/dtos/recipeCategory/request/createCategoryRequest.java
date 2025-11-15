package com.project.mealplan.dtos.recipeCategory.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class createCategoryRequest {
	@NotBlank(message = "Category name must not be empty")
	String name;
}
