package com.project.mealplan.controller.AdminController;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.recipeCategory.request.createCategoryRequest;
import com.project.mealplan.dtos.recipeCategory.request.updateCategoryRequest;
import com.project.mealplan.dtos.recipeCategory.response.RecipeCategoryDto;
import com.project.mealplan.service.RecipeCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/recipe/category")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management Recipes", description = "APIs for admin management operations about recipe")
@SecurityRequirement(name = "bearerAuth")
public class AdminRecipeCategoryController {
	private final RecipeCategoryService recipeCategoryService;
	
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Recipe Category", description = "Create a new recipe category")
    public ResponseEntity<ApiResponse<RecipeCategoryDto>> createRecipeCategory(@Valid @RequestBody createCategoryRequest request) {
        RecipeCategoryDto createdCategory = recipeCategoryService.createCategory(request);
        ApiResponse<RecipeCategoryDto> response = new ApiResponse<>(
                                        HttpStatus.CREATED.value(),
                                        "Recipe Category created successfully",
                                        createdCategory);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Recipe Category", description = "Update an existing recipe category")
    public ResponseEntity<ApiResponse<RecipeCategoryDto>> updateRecipeCategory(@Valid @RequestBody updateCategoryRequest request) {
        RecipeCategoryDto updatedCategory = recipeCategoryService.updateCategory(request);
        ApiResponse<RecipeCategoryDto> response = new ApiResponse<>(
                                        HttpStatus.OK.value(),
                                        "Recipe Category updated successfully",
                                        updatedCategory);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Recipe Category", description = "Delete a recipe category by ID")
    public ResponseEntity<ApiResponse<Void>> deleteRecipeCategory(@PathVariable Long id) {
        recipeCategoryService.deleteRecipeCategory(id);
        ApiResponse<Void> response = new ApiResponse<>(
                                        HttpStatus.OK.value(),
                                        "Recipe Category deleted successfully",
                                        null);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }   
}
