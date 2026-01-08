package com.project.mealplan.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.enums.IngredientUnit;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.common.util.UnitConverter;
import com.project.mealplan.dtos.shoppinglist.request.GenerateShoppingListRequest;
import com.project.mealplan.dtos.shoppinglist.request.ShoppingListItemUpdateRequest;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListItemResponse;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListItemResponseConverter;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListResponse;
import com.project.mealplan.dtos.shoppinglist.response.ShoppingListResponseConveter;
import com.project.mealplan.entity.*;
import com.project.mealplan.repository.*;
import com.project.mealplan.service.ShoppingListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final MealPlanRepository mealPlanRepository;
    private final PantryRepository pantryRepository;
    private final PantryItemRepository pantryItemRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final ShoppingListResponseConveter shoppingListResponseConveter;
    private final ShoppingListItemResponseConverter shoppingListItemResponseConverter;

    @Override
    @Transactional
    public ShoppingListResponse generateShoppingList(Long userId, GenerateShoppingListRequest request) {
        log.info("Generating shopping list for user: {}, mealPlanId: {}", userId, request.getMealPlanId());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 1. Validate meal plan exists and belongs to user
        MealPlan mealPlan = mealPlanRepository.findById(request.getMealPlanId())
                .orElseThrow(() -> new AppException(ErrorCode.MEAL_PLAN_NOT_FOUND));

        if (!mealPlan.getUser().getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_ACCESS_SHOPPING_LIST);
        }

        // 2. Check if meal plan is expired
        if (mealPlan.getEndDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.MEAL_PLAN_EXPIRED);
        }

        // 3. Get or create user's shopping list
        ShoppingList shoppingList = getOrCreateShoppingList(user);

        // 4. Aggregate all ingredients from meal plan recipes
        Map<Long, IngredientRequirement> requiredIngredients = aggregateMealPlanIngredients(mealPlan);

        // 5. Get pantry items and subtract from requirements
        subtractPantryItems(userId, requiredIngredients);

        // 6. Merge new items into existing shopping list
        mergeItemsIntoShoppingList(shoppingList, requiredIngredients);

        ShoppingList savedList = shoppingListRepository.save(shoppingList);
        log.info("Shopping list updated with id: {} and {} items", savedList.getId(), savedList.getItems().size());

        return shoppingListResponseConveter.convert(savedList);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingListResponse getShoppingList(Long userId) {
        ShoppingList shoppingList = shoppingListRepository.findByUser_UserId(userId)
                .orElse(null);

        if (shoppingList == null) {
            // Return empty response if no shopping list exists
            return ShoppingListResponse.builder()
                    .items(Collections.emptyList())
                    .build();
        }

        return shoppingListResponseConveter.convert(shoppingList);
    }

    @Override
    @Transactional
    public void clearShoppingList(Long userId) {
        ShoppingList shoppingList = shoppingListRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOPPING_LIST_NOT_FOUND));

        shoppingList.clearItems();
        shoppingListRepository.save(shoppingList);
        log.info("Shopping list cleared for user: {}", userId);
    }

    @Override
    @Transactional
    public ShoppingListItemResponse updateShoppingListItem(Long userId, Long itemId,
            ShoppingListItemUpdateRequest request) {
        ShoppingListItem item = shoppingListItemRepository.findByIdAndShoppingList_User_UserId(itemId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOPPING_LIST_ITEM_NOT_FOUND));

        // Handle quantity update
        if (request.getQuantity() != null) {
            if (request.getQuantity() <= 0) {
                // Delete item if quantity <= 0
                shoppingListItemRepository.delete(item);
                return null;
            }
            item.setQuantity(request.getQuantity());
        }

        // Handle unit update
        if (request.getUnit() != null) {
            item.setUnit(request.getUnit());
        }

        // Handle isChecked update - add to pantry if checked
        if (request.getIsChecked() != null && request.getIsChecked() && !item.getIsChecked()) {
            // Item is being marked as checked (bought) - add to pantry
            addToPantry(userId, item);

            // Remove item from shopping list after adding to pantry
            shoppingListItemRepository.delete(item);
            log.info("Shopping list item {} checked and added to pantry", itemId);
            return null;
        }

        if (request.getIsChecked() != null) {
            item.setIsChecked(request.getIsChecked());
        }

        ShoppingListItem savedItem = shoppingListItemRepository.save(item);
        return shoppingListItemResponseConverter.convert(savedItem);
    }

    @Override
    @Transactional
    public void deleteShoppingListItem(Long userId, Long itemId) {
        ShoppingListItem item = shoppingListItemRepository.findByIdAndShoppingList_User_UserId(itemId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.SHOPPING_LIST_ITEM_NOT_FOUND));
        shoppingListItemRepository.delete(item);
        log.info("Shopping list item deleted: {}", itemId);
    }

    @Override
    @Transactional
    public void bulkCheckItems(Long userId, List<Long> itemIds) {
        log.info("Bulk checking {} items for user {}", itemIds.size(), userId);
        for (Long itemId : itemIds) {
            ShoppingListItem item = shoppingListItemRepository.findByIdAndShoppingList_User_UserId(itemId, userId)
                    .orElse(null);
            if (item != null) {
                addToPantry(userId, item);
                shoppingListItemRepository.delete(item);
            }
        }
        log.info("Bulk check completed for {} items", itemIds.size());
    }

    @Override
    @Transactional
    public void bulkDeleteItems(Long userId, List<Long> itemIds) {
        log.info("Bulk deleting {} items for user {}", itemIds.size(), userId);
        for (Long itemId : itemIds) {
            shoppingListItemRepository.findByIdAndShoppingList_User_UserId(itemId, userId)
                    .ifPresent(shoppingListItemRepository::delete);
        }
        log.info("Bulk delete completed for {} items", itemIds.size());
    }

    @Override
    @Transactional
    public ShoppingListItemResponse addItem(Long userId, Long ingredientId, Double quantity, IngredientUnit unit) {
        log.info("Adding item to shopping list for user {}: ingredientId={}, quantity={}, unit={}",
                userId, ingredientId, quantity, unit);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

        // Get or create shopping list
        ShoppingList shoppingList = getOrCreateShoppingList(user);

        // Check if ingredient already exists in shopping list
        Optional<ShoppingListItem> existingItem = shoppingList.getItems().stream()
                .filter(item -> item.getIngredient().getId().equals(ingredientId) && item.getUnit() == unit)
                .findFirst();

        ShoppingListItem savedItem;
        if (existingItem.isPresent()) {
            // Update existing item - add to quantity
            ShoppingListItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            savedItem = shoppingListItemRepository.save(item);
            log.info("Updated existing shopping list item, new quantity: {}", item.getQuantity());
        } else {
            // Create new item
            ShoppingListItem newItem = new ShoppingListItem();
            newItem.setIngredient(ingredient);
            newItem.setQuantity(quantity);
            newItem.setUnit(unit);
            newItem.setIsChecked(false);
            shoppingList.addItem(newItem);
            savedItem = shoppingListItemRepository.save(newItem);
            log.info("Created new shopping list item with id: {}", savedItem.getId());
        }

        return shoppingListItemResponseConverter.convert(savedItem);
    }

    // =============== Private Helper Methods ===============

    /**
     * Get or create user's single shopping list
     */
    private ShoppingList getOrCreateShoppingList(User user) {
        return shoppingListRepository.findByUser_UserId(user.getUserId())
                .orElseGet(() -> {
                    ShoppingList newList = new ShoppingList();
                    newList.setUser(user);
                    return shoppingListRepository.save(newList);
                });
    }

    /**
     * Merge new ingredient requirements into existing shopping list
     */
    private void mergeItemsIntoShoppingList(ShoppingList shoppingList, Map<Long, IngredientRequirement> requirements) {
        // Create a map of existing items by ingredient ID for quick lookup
        Map<Long, ShoppingListItem> existingItems = new HashMap<>();
        for (ShoppingListItem item : shoppingList.getItems()) {
            existingItems.put(item.getIngredient().getId(), item);
        }

        // Add or update items
        for (IngredientRequirement req : requirements.values()) {
            if (req.quantityInGrams.compareTo(BigDecimal.ZERO) > 0) {
                ShoppingListItem existingItem = existingItems.get(req.ingredientId);

                if (existingItem != null) {
                    // Update existing item - add to quantity
                    double newQuantity = existingItem.getQuantity() + req.quantityInGrams.doubleValue();
                    existingItem.setQuantity(Math.round(newQuantity * 100.0) / 100.0);
                } else {
                    // Create new item
                    Ingredient ingredient = ingredientRepository.findById(req.ingredientId)
                            .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

                    ShoppingListItem newItem = new ShoppingListItem();
                    newItem.setIngredient(ingredient);
                    newItem.setQuantity(req.quantityInGrams.setScale(2, RoundingMode.HALF_UP).doubleValue());
                    newItem.setUnit(IngredientUnit.G);
                    newItem.setIsChecked(false);
                    shoppingList.addItem(newItem);
                }
            }
        }
    }

    /**
     * Add shopping list item to pantry when checked as bought
     */
    private void addToPantry(Long userId, ShoppingListItem shoppingItem) {
        Pantry pantry = pantryRepository.findByUser_UserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                    Pantry newPantry = new Pantry();
                    newPantry.setUser(user);
                    return pantryRepository.save(newPantry);
                });

        // Check if ingredient already exists in pantry with same unit
        Optional<PantryItem> existingPantryItem = pantry.getItems().stream()
                .filter(item -> item.getIngredient().getId().equals(shoppingItem.getIngredient().getId())
                        && item.getUnit() == shoppingItem.getUnit())
                .findFirst();

        if (existingPantryItem.isPresent()) {
            // Add to existing pantry item
            PantryItem pantryItem = existingPantryItem.get();
            pantryItem.setQuantity(pantryItem.getQuantity() + shoppingItem.getQuantity());
            pantryItemRepository.save(pantryItem);
        } else {
            // Create new pantry item
            PantryItem newPantryItem = new PantryItem();
            newPantryItem.setPantry(pantry);
            newPantryItem.setIngredient(shoppingItem.getIngredient());
            newPantryItem.setQuantity(shoppingItem.getQuantity());
            newPantryItem.setUnit(shoppingItem.getUnit());
            newPantryItem.setExpiresAt(LocalDate.now().plusDays(7)); // Default expiry: 7 days
            pantry.getItems().add(newPantryItem);
            pantryItemRepository.save(newPantryItem);
        }

        log.info("Added ingredient {} to pantry: {} {}",
                shoppingItem.getIngredient().getName(),
                shoppingItem.getQuantity(),
                shoppingItem.getUnit());
    }

    /**
     * Aggregate all ingredients from meal plan recipes
     */
    private Map<Long, IngredientRequirement> aggregateMealPlanIngredients(MealPlan mealPlan) {
        Map<Long, IngredientRequirement> requirements = new HashMap<>();

        for (MealDay day : mealPlan.getMealDays()) {
            for (MealSlot slot : day.getMealSlots()) {
                Recipe recipe = slot.getRecipe();
                Double slotQuantity = slot.getQuantity() != null ? slot.getQuantity() : 1.0;

                for (RecipeIngredient ri : recipe.getIngredients()) {
                    Long ingredientId = ri.getIngredient().getId();
                    BigDecimal density = ri.getIngredient().getDensity();
                    BigDecimal quantityInGrams = UnitConverter.toGram(
                            BigDecimal.valueOf(ri.getQuantity() * slotQuantity),
                            ri.getUnit(),
                            density);

                    requirements.compute(ingredientId, (id, existing) -> {
                        if (existing == null) {
                            return new IngredientRequirement(ingredientId, quantityInGrams);
                        }
                        existing.quantityInGrams = existing.quantityInGrams.add(quantityInGrams);
                        return existing;
                    });
                }
            }
        }

        return requirements;
    }

    /**
     * Subtract pantry items from required ingredients
     */
    private void subtractPantryItems(Long userId, Map<Long, IngredientRequirement> requirements) {
        pantryRepository.findByUser_UserId(userId).ifPresent(pantry -> {
            for (PantryItem pantryItem : pantry.getItems()) {
                Long ingredientId = pantryItem.getIngredient().getId();
                IngredientRequirement req = requirements.get(ingredientId);

                if (req != null) {
                    BigDecimal density = pantryItem.getIngredient().getDensity();
                    BigDecimal pantryQuantityInGrams = UnitConverter.toGram(
                            BigDecimal.valueOf(pantryItem.getQuantity()),
                            pantryItem.getUnit(),
                            density);
                    req.quantityInGrams = req.quantityInGrams.subtract(pantryQuantityInGrams);
                }
            }
        });
    }

    // =============== Inner Classes ===============

    private static class IngredientRequirement {
        Long ingredientId;
        BigDecimal quantityInGrams;

        IngredientRequirement(Long ingredientId, BigDecimal quantityInGrams) {
            this.ingredientId = ingredientId;
            this.quantityInGrams = quantityInGrams;
        }
    }
}
