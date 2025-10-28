package com.project.mealplan.controller;

import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.common.response.PagePayLoad;
import com.project.mealplan.config.CustomUserDetails;
import com.project.mealplan.dtos.recipe.response.RecipeResponseDto;
import com.project.mealplan.dtos.recipe.response.RecipeShortResponse;
import com.project.mealplan.security.CurrentUser;
import com.project.mealplan.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Recipe", description = "Recipe APIs")
@SecurityRequirement(name = "bearerAuth")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get recipe by id")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> getRecipeById(@PathVariable Long id) {

        RecipeResponseDto recipe = recipeService.getRecipeById(id);

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
        
        if (cu.isUser() && status == RecipeStatus.DRAFT) {
            return ResponseEntity.status(403).body(
                ApiResponse.<PagePayLoad<RecipeShortResponse>>builder()
                    .status(403)
                    .message("Forbidden: Users cannot access DRAFT recipes")
                    .build()
            );
        }
        
        Page<RecipeShortResponse> result = recipeService.getRecipes(cu, status, category, minCalories, maxCalories, page, size, sortBy, sortDir, keyword);

        PagePayLoad<RecipeShortResponse> payload = PagePayLoad.of(result);

        return ResponseEntity.ok(
                ApiResponse.<PagePayLoad<RecipeShortResponse>>builder()
                        .status(200)
                        .message("Recipes retrieved successfully")
                        .data(payload)
                        .build());
    }
}
