package com.project.mealplan.controller;

import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.common.response.PagePayLoad;
import com.project.mealplan.config.CustomUserDetails;
import com.project.mealplan.dtos.recipe.request.RecipeCreateRequest;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeDto;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeStatus;
import com.project.mealplan.dtos.recipe.response.RecipeResponseDto;
import com.project.mealplan.dtos.recipe.response.RecipeShortResponse;
import com.project.mealplan.security.CurrentUser;
import com.project.mealplan.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recipe", description = "Common APIs for recipes")
@SecurityRequirement(name = "bearerAuth")
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Add recipe", description = "Add a new recipe with comprehensive validation")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> createRecipe(
                    @Valid @RequestBody RecipeCreateRequest request,
                    @AuthenticationPrincipal CustomUserDetails principal) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        CurrentUser cu = new CurrentUser(principal.getId(), roles);

        RecipeResponseDto resp = recipeService.createRecipe(request, cu);

        ApiResponse<RecipeResponseDto> response = new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Recipe created successfully",
                        resp);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get recipe by id")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> getRecipeById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        CurrentUser cu = new CurrentUser(principal.getId(), roles);

        RecipeResponseDto recipe = recipeService.getRecipeById(id, cu);

        return ResponseEntity.ok(ApiResponse.<RecipeResponseDto>builder()
                .status(200)
                .message("Recipe retrieved successfully")
                .data(recipe)
                .build());
    }

            
    @Operation(summary = "Search & list recipes with filters/pagination")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<PagePayLoad<RecipeShortResponse>>> getRecipeList(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(required = false) RecipeStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minCookingTimeMinutes,
            @RequestParam(required = false) Integer maxCookingTimeMinutes,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) BigDecimal minCalories,
            @RequestParam(required = false) BigDecimal maxCalories,
            @RequestParam(required = false, defaultValue = "title") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());

        CurrentUser cu = new CurrentUser(principal.getId(), roles);
        
        Page<RecipeShortResponse> result = recipeService.getRecipes(cu, status, category, mealType, minCookingTimeMinutes, maxCookingTimeMinutes, minCalories, maxCalories, page, size, sortBy, sortDir, keyword);

        PagePayLoad<RecipeShortResponse> payload = PagePayLoad.of(result);

        return ResponseEntity.ok(
                ApiResponse.<PagePayLoad<RecipeShortResponse>>builder()
                        .status(200)
                        .message("Recipes retrieved successfully")
                        .data(payload)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Update recipe by id")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecipeDto dto,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        CurrentUser cu = new CurrentUser(principal.getId(), roles);

        RecipeResponseDto updated = recipeService.updateRecipe(id, dto, cu);

        return ResponseEntity.ok(ApiResponse.<RecipeResponseDto>builder()
                .status(200)
                .message("Recipe updated successfully")
                .data(updated)
                .build());
    }
    
        
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Delete recipe by id")
    public ResponseEntity<ApiResponse<Object>> deleteRecipe(
        @PathVariable Long id, 
        @AuthenticationPrincipal CustomUserDetails principal) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        CurrentUser cu = new CurrentUser(principal.getId(), roles);

        recipeService.deleteRecipe(id, cu);

        return ResponseEntity.ok(ApiResponse.<Object>builder()
                .status(200)
                .message("Xóa thành công")
                .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update recipe status to PENDING or DRAFT by id")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> updateRecipeStatus(
            @PathVariable Long id,
            @RequestBody UpdateRecipeStatus status,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        CurrentUser cu = new CurrentUser(principal.getId(), roles);

        RecipeResponseDto updated = recipeService.updateRecipeStatus(id, status, cu);

        return ResponseEntity.ok(ApiResponse.<RecipeResponseDto>builder()
                .status(200)
                .message("Recipe status updated successfully")
                .data(updated)
                .build());
    }

}
