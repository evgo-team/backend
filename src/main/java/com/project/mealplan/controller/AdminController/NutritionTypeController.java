package com.project.mealplan.controller.AdminController;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.entity.NutritionType;
import com.project.mealplan.service.IngredientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/ingredients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management Ingredients", description = "APIs for admin management operations about ingredients")
@SecurityRequirement(name = "bearerAuth")
public class NutritionTypeController {

		private final IngredientService ingredientService;

		@Operation(summary = "Get nutrition list")
		@GetMapping("/nutritions")
		@PreAuthorize("hasRole('ADMIN')")
		public ResponseEntity<ApiResponse<List<NutritionType>>> getAllNutritions() {
						List<NutritionType> nutritions = ingredientService.getAllNutritions();
						ApiResponse<List<NutritionType>> response = new ApiResponse<>(
														HttpStatus.CREATED.value(),
														"Nutriton get successfully",
														nutritions);

						return new ResponseEntity<>(response, HttpStatus.OK);
		}
}
