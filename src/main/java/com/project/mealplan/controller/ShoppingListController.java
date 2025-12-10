package com.project.mealplan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.shoppinglist.request.GenerateShoppingListRequest;
import com.project.mealplan.dtos.shoppinglist.request.ShoppingListItemUpdateRequest;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListItemResponse;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListResponse;
import com.project.mealplan.security.jwt.SecurityUtil;
import com.project.mealplan.service.ShoppingListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
@Tag(name = "Shopping List", description = "APIs for shopping list management")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ShoppingListController {

        private final ShoppingListService shoppingListService;

        @Operation(summary = "Generate shopping list from meal plan", description = "Creates or updates user's shopping list by adding ingredients from the specified meal plan (minus pantry items)")
        @PostMapping("/generate")
        public ResponseEntity<ApiResponse<ShoppingListResponse>> generateShoppingList(
                        @RequestBody @Valid GenerateShoppingListRequest request) {
                Long currentUserId = SecurityUtil.getCurrentUserId();
                ShoppingListResponse response = shoppingListService.generateShoppingList(currentUserId, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ShoppingListResponse>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Shopping list generated successfully")
                                .data(response)
                                .build());
        }

        @Operation(summary = "Get current user's shopping list", description = "Returns the user's single shopping list with all items")
        @GetMapping
        public ResponseEntity<ApiResponse<ShoppingListResponse>> getShoppingList() {
                Long currentUserId = SecurityUtil.getCurrentUserId();
                ShoppingListResponse response = shoppingListService.getShoppingList(currentUserId);
                return ResponseEntity.ok(ApiResponse.<ShoppingListResponse>builder()
                                .status(HttpStatus.OK.value())
                                .message("Shopping list retrieved successfully")
                                .data(response)
                                .build());
        }

        @Operation(summary = "Clear all items from shopping list")
        @DeleteMapping
        public ResponseEntity<ApiResponse<Void>> clearShoppingList() {
                Long currentUserId = SecurityUtil.getCurrentUserId();
                shoppingListService.clearShoppingList(currentUserId);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Shopping list cleared successfully")
                                .build());
        }

        @Operation(summary = "Update a shopping list item", description = "Update quantity, unit, or mark as checked. When checked, item is moved to pantry and removed from shopping list.")
        @PutMapping("/items/{itemId}")
        public ResponseEntity<ApiResponse<ShoppingListItemResponse>> updateShoppingListItem(
                        @PathVariable Long itemId,
                        @RequestBody ShoppingListItemUpdateRequest request) {
                Long currentUserId = SecurityUtil.getCurrentUserId();
                ShoppingListItemResponse response = shoppingListService.updateShoppingListItem(currentUserId, itemId,
                                request);

                if (response == null) {
                        String message;
                        if (request.getQuantity() != null && request.getQuantity() <= 0) {
                                message = "Shopping list item deleted because quantity <= 0";
                        } else if (request.getIsChecked() != null && request.getIsChecked()) {
                                message = "Item checked and added to pantry";
                        } else {
                                message = "Shopping list item deleted";
                        }

                        return ResponseEntity.ok(ApiResponse.<ShoppingListItemResponse>builder()
                                        .status(HttpStatus.OK.value())
                                        .message(message)
                                        .data(null)
                                        .build());
                }

                return ResponseEntity.ok(ApiResponse.<ShoppingListItemResponse>builder()
                                .status(HttpStatus.OK.value())
                                .message("Shopping list item updated successfully")
                                .data(response)
                                .build());
        }

        @Operation(summary = "Delete a shopping list item")
        @DeleteMapping("/items/{itemId}")
        public ResponseEntity<ApiResponse<Void>> deleteShoppingListItem(@PathVariable Long itemId) {
                Long currentUserId = SecurityUtil.getCurrentUserId();
                shoppingListService.deleteShoppingListItem(currentUserId, itemId);
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                                .status(HttpStatus.OK.value())
                                .message("Shopping list item deleted successfully")
                                .build());
        }
}
