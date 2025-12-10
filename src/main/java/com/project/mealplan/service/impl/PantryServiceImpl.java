package com.project.mealplan.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.dtos.pantry.request.PantryItemRequest;
import com.project.mealplan.dtos.pantry.response.PantryItemResponse;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.Pantry;
import com.project.mealplan.entity.PantryItem;
import com.project.mealplan.entity.User;
import com.project.mealplan.repository.IngredientRepository;
import com.project.mealplan.repository.PantryItemRepository;
import com.project.mealplan.repository.PantryRepository;
import com.project.mealplan.repository.UserRepository;
import com.project.mealplan.service.PantryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PantryServiceImpl implements PantryService {

    private final PantryRepository pantryRepository;
    private final PantryItemRepository pantryItemRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public List<PantryItemResponse> getPantryItemsByUserId(Long userId) {
        Pantry pantry = getOrCreatePantry(userId);
        return pantry.getItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PantryItemResponse addPantryItem(Long userId, PantryItemRequest request) {
        Pantry pantry = getOrCreatePantry(userId);
        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId())
                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

        Optional<PantryItem> existingItem = pantry.getItems().stream()
                .filter(item -> item.getIngredient().getId().equals(ingredient.getId())
                        && item.getUnit() == request.getUnit())
                .findFirst();

        PantryItem item;
        if (existingItem.isPresent()) {
            item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            if (request.getExpiresAt() != null) {
                item.setExpiresAt(request.getExpiresAt());
            }
        } else {
            item = new PantryItem();
            item.setPantry(pantry);
            item.setIngredient(ingredient);
            item.setQuantity(request.getQuantity());
            item.setUnit(request.getUnit());
            item.setExpiresAt(request.getExpiresAt() != null ? request.getExpiresAt() : LocalDate.now());
            pantry.getItems().add(item);
        }

        PantryItem savedItem = pantryItemRepository.save(item);
        return mapToResponse(savedItem);
    }

    @Transactional
    public PantryItemResponse updatePantryItem(Long userId, Long itemId, PantryItemRequest request) {
        Pantry pantry = getOrCreatePantry(userId);
        PantryItem item = pantryItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.PANTRY_ITEM_NOT_FOUND));

        if (!item.getPantry().getId().equals(pantry.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS_PANTRY);
        }

        if (request.getQuantity() <= 0) {
            pantry.getItems().remove(item);
            pantryItemRepository.delete(item);
            return null;
        }

        item.setQuantity(request.getQuantity());
        if (request.getExpiresAt() != null) {
            item.setExpiresAt(request.getExpiresAt());
        }

        PantryItem savedItem = pantryItemRepository.save(item);
        return mapToResponse(savedItem);
    }

    @Transactional
    public void deletePantryItem(Long userId, Long itemId) {
        Pantry pantry = getOrCreatePantry(userId);
        PantryItem item = pantryItemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.PANTRY_ITEM_NOT_FOUND));

        if (!item.getPantry().getId().equals(pantry.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS_PANTRY);
        }

        pantry.getItems().remove(item);
        pantryItemRepository.delete(item);
    }

    private Pantry getOrCreatePantry(Long userId) {
        return pantryRepository.findByUser_UserId(userId)
                .orElseGet(() -> createPantryForUser(userId));
    }

    private Pantry createPantryForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pantry pantry = new Pantry();
        pantry.setUser(user);
        return pantryRepository.save(pantry);
    }

    private PantryItemResponse mapToResponse(PantryItem item) {
        return new PantryItemResponse(
                item.getId(),
                item.getIngredient().getId(),
                item.getIngredient().getName(),
                item.getQuantity(),
                item.getUnit(),
                item.getExpiresAt());
    }
}
