package com.project.mealplan.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.mealplan.common.response.ApiResponse;
import com.project.mealplan.dtos.PantryItemRequest;
import com.project.mealplan.dtos.PantryItemResponse;
import com.project.mealplan.security.jwt.SecurityUtil;
import com.project.mealplan.service.impl.PantryServiceImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pantry")
@RequiredArgsConstructor
@Tag(name = "Pantry", description = "Common APIs for pantry")
@SecurityRequirement(name = "bearerAuth")
public class PantryController {

    private final PantryServiceImpl pantryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PantryItemResponse>>> getPantry() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<PantryItemResponse> result = pantryService.getPantryItemsByUserId(currentUserId);
        return ResponseEntity.ok(ApiResponse.<List<PantryItemResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Get pantry successfully")
                .data(result)
                .build());
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<PantryItemResponse>> addPantryItem(
            @RequestBody PantryItemRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        PantryItemResponse response = pantryService.addPantryItem(currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<PantryItemResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Add pantry item successfully")
                .data(response)
                .build());
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<PantryItemResponse>> updatePantryItem(
            @PathVariable Long id,
            @RequestBody PantryItemRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        PantryItemResponse response = pantryService.updatePantryItem(currentUserId, id, request);
        if (response == null) {
            return ResponseEntity.ok(ApiResponse.<PantryItemResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Pantry item deleted because quantity <= 0")
                    .data(null)
                    .build());
        }
        return ResponseEntity.ok(ApiResponse.<PantryItemResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Update pantry item successfully")
                .data(response)
                .build());
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePantryItem(
            @PathVariable Long id) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        pantryService.deletePantryItem(currentUserId, id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Delete pantry item successfully")
                .build());
    }
}
