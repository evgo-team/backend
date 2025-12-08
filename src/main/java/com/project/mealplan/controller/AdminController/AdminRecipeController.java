package com.project.mealplan.controller.AdminController;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.recipe.DeleteRecipesDto;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeStatus;
import com.project.mealplan.dtos.recipe.response.RecipeResponseDto;
import com.project.mealplan.config.CustomUserDetails;
import com.project.mealplan.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;
import com.project.mealplan.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/recipes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management Recipes", description = "APIs for admin management operations about recipe")
@SecurityRequirement(name = "bearerAuth")
public class AdminRecipeController {
    private final RecipeService recipeService;
    
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

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin: Update recipe status to PUBLISHED or DRAFT by id")
    public ResponseEntity<ApiResponse<RecipeResponseDto>> adminUpdateRecipeStatus(
            @PathVariable Long id,
            @RequestBody UpdateRecipeStatus status,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Set<String> roles = principal.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        CurrentUser cu = new CurrentUser(principal.getId(), roles);

        RecipeResponseDto updated = recipeService.updateRecipeStatus(id, status, cu);

        return ResponseEntity.ok(ApiResponse.<RecipeResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Recipe status updated successfully by admin")
                .data(updated)
                .build());
    }
}
