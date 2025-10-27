package com.project.mealplan.controller.AdminController;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.recipe.DeleteRecipesDto;
import com.project.mealplan.dtos.recipe.request.RecipeCreateRequest;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeDto;
import com.project.mealplan.dtos.recipe.response.RecipeResponseDto;
import com.project.mealplan.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/recipes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recipe", description = "Recipe APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminRecipeController {
    private final RecipeService recipeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add recipe", description = "Add a new recipe with comprehensive validation")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> createIngredient(
                    @Valid @RequestBody RecipeCreateRequest request) {

            RecipeResponseDto resp = recipeService.createRecipe(request);

            ApiResponse<RecipeResponseDto> response = new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            "Recipe created successfully",
                            resp);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Update recipe by id")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecipeDto dto) {

        RecipeResponseDto updated = recipeService.updateRecipe(id, dto);

        return ResponseEntity.ok(ApiResponse.<RecipeResponseDto>builder()
                .status(200)
                .message("Recipe updated successfully")
                .data(updated)
                .build());
    }
    
        
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete recipe by id")
    public ResponseEntity<ApiResponse<Object>> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);

        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .status(200)
                .message("Xóa thành công")
                .build());
    }
    
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete multiple recipes by ids")
    public ResponseEntity<ApiResponse<Integer>> deleteRecipes(
        @RequestParam(value = "ids", required = false) List<Long> idsQuery,
        @RequestBody(required = false) DeleteRecipesDto body) {

        

        int deleted = recipeService.deleteRecipesByIds(idsQuery, body);

        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .status(200)
                .message("Deleted " + deleted + " recipes")
                .data(deleted)
                .build());
    } 
}
