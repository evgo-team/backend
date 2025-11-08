package com.project.mealplan.controller.AdminController;
import org.springframework.data.domain.Page;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.common.response.PagePayLoad;
import com.project.mealplan.dtos.admin.BulkDeleteIngredientDto;
import com.project.mealplan.dtos.admin.BulkDeleteIngredientResponseDto;
import com.project.mealplan.dtos.admin.IngredientResponseDto;
import com.project.mealplan.dtos.admin.UpdateIngredientDto;
import com.project.mealplan.dtos.ingredient.request.IngredientCreateRequest;
import com.project.mealplan.dtos.ingredient.response.IngredientListItemResponse;
import com.project.mealplan.dtos.ingredient.response.IngredientResponse;
import com.project.mealplan.service.IngredientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/ingredients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Management", description = "APIs for admin management operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminIngredientController {

        private final IngredientService ingredientService;

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Add ingredient", description = "Add a new ingredient with comprehensive validation")
        public ResponseEntity<ApiResponse<IngredientResponse>> createIngredient(
                        @Valid @RequestBody IngredientCreateRequest request) {

                IngredientResponse resp = ingredientService.createIngredient(request);

                ApiResponse<IngredientResponse> response = new ApiResponse<>(
                                HttpStatus.CREATED.value(),
                                "Ingredient created successfully",
                                resp);

                return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Update ingredient", description = "Update an existing ingredient with comprehensive validation")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingredient not found")
        })
        public ResponseEntity<ApiResponse<IngredientResponseDto>> updateIngredient(
                        @Parameter(description = "Ingredient ID", required = true) @PathVariable Long id,
                        @Parameter(description = "Updated ingredient data", required = true) @Valid @RequestBody UpdateIngredientDto updateDto) {

                log.info("Admin updating ingredient with ID: {}", id);
                log.debug("Update data: {}", updateDto);

                IngredientResponseDto updatedIngredient = ingredientService.updateIngredient(id, updateDto);

                return ResponseEntity.ok(ApiResponse.<IngredientResponseDto>builder()
                                .status(200)
                                .message("Ingredient updated successfully")
                                .data(updatedIngredient)
                                .build());
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Delete ingredient", description = "Delete an existing ingredient and all its nutrition data")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ingredient deleted successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Ingredient not found"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Ingredient is being used and cannot be deleted")
        })
        public ResponseEntity<ApiResponse<Void>> deleteIngredient(
                        @Parameter(description = "Ingredient ID", required = true) @PathVariable Long id) {

                log.info("Admin deleting ingredient with ID: {}", id);

                ingredientService.deleteIngredient(id);

                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(200)
                                .message("Ingredient deleted successfully")
                                .data(null)
                                .build());
        }

        @DeleteMapping
        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Bulk delete ingredients", description = "Delete multiple ingredients by IDs in a single transaction")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bulk delete completed (check response for individual results)"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data - empty ID list"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
        })
        public ResponseEntity<ApiResponse<BulkDeleteIngredientResponseDto>> bulkDeleteIngredients(
                        @Parameter(description = "Bulk delete request with ingredient IDs", required = true) @Valid @RequestBody BulkDeleteIngredientDto bulkDeleteDto) {

                log.info("Admin bulk deleting ingredients with IDs: {}", bulkDeleteDto.getIds());

                BulkDeleteIngredientResponseDto result = ingredientService.bulkDeleteIngredients(bulkDeleteDto);

                // Determine response message based on results
                String message;
                if (result.getFailed() == 0) {
                        message = "All " + result.getSuccessfullyDeleted() + " ingredients deleted successfully";
                } else if (result.getSuccessfullyDeleted() == 0) {
                        message = "No ingredients were deleted - all IDs were invalid";
                } else {
                        message = String.format("Partial success: %d deleted, %d failed",
                                        result.getSuccessfullyDeleted(), result.getFailed());
                }

                return ResponseEntity.ok(ApiResponse.<BulkDeleteIngredientResponseDto>builder()
                                .status(200)
                                .message(message)
                                .data(result)
                                .build());
        }

        @Operation(summary = "Get ingredient detail by ID")
        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ApiResponse<IngredientResponse>> getIngredientById(
                        @PathVariable Long id) {

                IngredientResponse data = ingredientService.getIngredientDetailById(id);

                return ResponseEntity.ok(
                                ApiResponse.<IngredientResponse>builder()
                                                .status(200)
                                                .message("Ingredient detail retrieved successfully")
                                                .data(data)
                                                .build());
        }
        
        @Operation(summary = "Search & list ingredients with filters/pagination")
        @GetMapping("")
        @PreAuthorize("hasRole('ADMIN')")
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