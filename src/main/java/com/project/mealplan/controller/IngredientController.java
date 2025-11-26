package com.project.mealplan.controller;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.common.response.PagePayLoad;
import com.project.mealplan.dtos.ingredient.response.IngredientListItemResponse;
import com.project.mealplan.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin/User view Ingredients", description = "APIs for user operations about ingredients")
@SecurityRequirement(name = "bearerAuth")
public class IngredientController {

    private final IngredientService ingredientService;

    @Operation(summary = "Search & list ingredients with filters/pagination")
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ApiResponse<PagePayLoad<IngredientListItemResponse>>> listIngredients(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minCalories,
            @RequestParam(required = false) BigDecimal maxCalories,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<IngredientListItemResponse> result = ingredientService.searchIngredients(
                type, minCalories, maxCalories, keyword, page, size, sortBy, sortDir);

        PagePayLoad<IngredientListItemResponse> payload = PagePayLoad.of(result);

        return ResponseEntity.ok(
                ApiResponse.<PagePayLoad<IngredientListItemResponse>>builder()
                        .status(200)
                        .message("Ingredients retrieved successfully")
                        .data(payload)
                        .build());
    }
}
