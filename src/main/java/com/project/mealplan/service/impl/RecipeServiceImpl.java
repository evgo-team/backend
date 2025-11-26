package com.project.mealplan.service.impl;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.enums.IngredientUnit;
import com.project.mealplan.common.enums.RecipeStatus;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.dtos.recipe.request.RecipeCreateRequest;
import com.project.mealplan.dtos.recipe.request.RecipeIngredientRequest;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeDto;
import com.project.mealplan.dtos.recipe.request.UpdateRecipeStatus;
import com.project.mealplan.dtos.recipe.response.RecipeResponseDto;
import com.project.mealplan.dtos.recipe.response.RecipeShortResponse;
import com.project.mealplan.dtos.recipe.DeleteRecipesDto;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.Recipe;
import com.project.mealplan.entity.RecipeCategory;
import com.project.mealplan.entity.RecipeIngredient;
import com.project.mealplan.entity.User;
import com.project.mealplan.repository.IngredientRepository;
import com.project.mealplan.repository.RecipeCategoryRepository;
import com.project.mealplan.repository.RecipeRepository;
import com.project.mealplan.repository.UserRepository;
import com.project.mealplan.repository.spec.RecipeSpecifications;
import com.project.mealplan.security.CurrentUser;
import com.project.mealplan.service.RecipeService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import com.project.mealplan.common.util.CalculateCalories;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RecipeResponseDto createRecipe(RecipeCreateRequest request, CurrentUser currentUser) {
        if (request.getStatus() == RecipeStatus.PUBLISHED) {
            boolean exists = recipeRepository.existsByTitleAndStatus(request.getTitle(), RecipeStatus.PUBLISHED);
            if (exists) {
                throw new AppException(ErrorCode.RECIPE_TITLE_ALREADY_EXISTS);
            }
        }

        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Recipe recipe = new Recipe();
        recipe.setCreatedBy(author);
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setInstructions(request.getInstructions());
        recipe.setCookingTimeMinutes(request.getCookingTimeMinutes());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setRole(request.getRole());
        recipe.setMealType(request.getMealType());

        if (currentUser.isUser()) {
            recipe.setStatus(RecipeStatus.DRAFT);
        } else {
            // ADMIN có thể set status từ request, nếu không set thì mặc định là DRAFT
            recipe.setStatus(request.getStatus() != null ? request.getStatus() : RecipeStatus.DRAFT);
        }

        // Gán category
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<RecipeCategory> categories = request.getCategoryIds().stream()
                    .map(id -> categoryRepository.findById(id)
                            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)))
                    .collect(Collectors.toSet());
            recipe.setCategories(categories);
        }

        // Gán ingredients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            for (RecipeIngredientRequest ingReq : request.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findById(ingReq.getIngredientId())
                        .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

                IngredientUnit unit;
                try {
                    unit = IngredientUnit.valueOf(ingReq.getUnit().trim().toUpperCase());
                } catch (Exception e) {
                    throw new AppException(ErrorCode.INVALID_INGREDIENT_UNIT);
                }

                RecipeIngredient ri = new RecipeIngredient();
                ri.setIngredient(ingredient);
                ri.setQuantity(ingReq.getQuantity());
                ri.setUnit(unit);
                recipe.addIngredient(ri);
            }
        }

        // calculate calories before saving
        recipe.setCalories(CalculateCalories.computeRecipeCalories(recipe));
        Recipe saved = recipeRepository.save(recipe);
        return convertToDto(saved);
    } 

    @Override
    @Transactional
    public RecipeResponseDto updateRecipe(Long id, UpdateRecipeDto dto, CurrentUser currentUser) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        // Check permissions
        boolean isOwner = recipe.getCreatedBy() != null && recipe.getCreatedBy().getUserId().equals(currentUser.getId());

        if (currentUser.isUser()) {
            if (!isOwner) {
                throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền sửa công thức này");
            }
            if (recipe.getStatus() != RecipeStatus.DRAFT) {
                throw new AppException(ErrorCode.FORBIDDEN, "Bạn chỉ có thể sửa công thức khi ở trạng thái DRAFT");
            }
            // USER (owner, DRAFT) không được phép đổi status.
            if (dto.getStatus() != null && dto.getStatus() != RecipeStatus.DRAFT) {
                 throw new AppException(ErrorCode.VALIDATION_ERROR, "Bạn không có quyền thay đổi trạng thái của công thức.");
            }
        }


        boolean changed = false;

        // Validate and parse status
        RecipeStatus newStatus;
        try {
            newStatus = RecipeStatus.valueOf(dto.getStatus().name());
        } catch (Exception ex) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid recipe status");
        }

        // Title required (DTO validation should enforce), check uniqueness when publishing
        if (dto.getTitle() != null && !dto.getTitle().equals(recipe.getTitle())) {
            if (newStatus == RecipeStatus.PUBLISHED) {
                boolean exists = recipeRepository.existsByTitleAndStatus(dto.getTitle(), RecipeStatus.PUBLISHED);
                if (exists) {
                    throw new AppException(ErrorCode.RECIPE_TITLE_ALREADY_EXISTS);
                }
            }
            recipe.setTitle(dto.getTitle());
            changed = true;
        }

        if (dto.getDescription() != null && !Objects.equals(dto.getDescription(), recipe.getDescription())) {
            recipe.setDescription(dto.getDescription());
            changed = true;
        }

        if (dto.getInstructions() != null && !Objects.equals(dto.getInstructions(), recipe.getInstructions())) {
            recipe.setInstructions(dto.getInstructions());
            changed = true;
        }

        if (dto.getCookingTimeMinutes() != null && !Objects.equals(dto.getCookingTimeMinutes(), recipe.getCookingTimeMinutes())) {
            recipe.setCookingTimeMinutes(dto.getCookingTimeMinutes());
            changed = true;
        }

        // if (dto.getCategory() != null && !Objects.equals(dto.getCategory(), recipe.getCategory())) {
        //     recipe.setCategory(dto.getCategory());
        //     changed = true;
        // }

        if (dto.getImageUrl() != null && !Objects.equals(dto.getImageUrl(), recipe.getImageUrl())) {
            recipe.setImageUrl(dto.getImageUrl());
            changed = true;
        }

        // Status (ONLY ADMIN)
        if (currentUser.isAdmin() && dto.getStatus() != null && dto.getStatus() != recipe.getStatus()) {
            if (dto.getStatus() == RecipeStatus.PUBLISHED) {
                boolean exists = recipeRepository.existsByTitleAndStatus(recipe.getTitle(), RecipeStatus.PUBLISHED);
                if (exists && (recipe.getTitle() == null || !recipe.getTitle().equals(dto.getTitle()))) {
                    throw new AppException(ErrorCode.RECIPE_TITLE_ALREADY_EXISTS);
                }
            }
            recipe.setStatus(dto.getStatus());
            changed = true;
        }

        // Ingredients handling
        if (dto.getIngredients() != null) {
            // map existing by ingredient id
            Map<Long, RecipeIngredient> existingMap = recipe.getIngredients()
                    .stream()
                    .collect(Collectors.toMap(ri -> ri.getIngredient().getId(), ri -> ri));

            Set<Long> incomingIds = new HashSet<>();

            for (UpdateRecipeDto.IngredientEntry entry : dto.getIngredients()) {
                if (entry.getQuantity() == null || entry.getQuantity() <= 0) {
                    throw new AppException(ErrorCode.VALIDATION_ERROR, "Ingredient quantity must be greater than 0");
                }
                if (entry.getUnit() == null || entry.getUnit().isBlank()) {
                    throw new AppException(ErrorCode.VALIDATION_ERROR, "Ingredient unit is required");
                }

                Ingredient ing = ingredientRepository.findById(entry.getIngredientId())
                        .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

                incomingIds.add(entry.getIngredientId());

                IngredientUnit unit;
                try {
                    unit = IngredientUnit.valueOf(entry.getUnit().trim().toUpperCase());
                } catch (Exception e) {
                    throw new AppException(ErrorCode.INVALID_INGREDIENT_UNIT);
                }

                if (existingMap.containsKey(entry.getIngredientId())) {
                    RecipeIngredient existing = existingMap.get(entry.getIngredientId());
                    if (!Objects.equals(existing.getQuantity(), entry.getQuantity())
                            || !Objects.equals(existing.getUnit(), unit)) {
                        existing.setQuantity(entry.getQuantity());
                        existing.setUnit(unit);
                        changed = true;
                    }
                } else {
                    // add new
                    RecipeIngredient newRi = new RecipeIngredient();
                    newRi.setIngredient(ing);
                    newRi.setQuantity(entry.getQuantity());
                    newRi.setUnit(unit);
                    recipe.addIngredient(newRi);
                    changed = true;
                }
            }

            // remove ingredients not present in incoming list
            List<RecipeIngredient> toRemove = recipe.getIngredients()
                    .stream()
                    .filter(ri -> !incomingIds.contains(ri.getIngredient().getId()))
                    .collect(Collectors.toList());

            if (!toRemove.isEmpty()) {
                recipe.getIngredients().removeAll(toRemove);
                // JPA orphanRemoval + cascade should delete them
                changed = true;
            }
        }

        if (!changed) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Không có thay đổi nào để lưu.");
        }

        // Recalculate calories after potential ingredient/status changes
        recipe.setCalories(CalculateCalories.computeRecipeCalories(recipe));
        Recipe saved = recipeRepository.save(recipe);

        return convertToDto(saved);
    }

    private RecipeResponseDto convertToDto(Recipe recipe) {
        RecipeResponseDto dto = new RecipeResponseDto();
        dto.setRecipeId(recipe.getRecipeId());
        dto.setTitle(recipe.getTitle());
        dto.setDescription(recipe.getDescription());
        dto.setInstructions(recipe.getInstructions());
        dto.setCookingTimeMinutes(recipe.getCookingTimeMinutes());
        dto.setImageUrl(recipe.getImageUrl());
        dto.setStatus(recipe.getStatus() != null ? recipe.getStatus() : null);
        dto.setRole(recipe.getRole() != null ? recipe.getRole() : null);
        dto.setMealType(recipe.getMealType() != null ? recipe.getMealType() : null);
        dto.setCreatedBy(recipe.getCreatedBy().getFullName());
        dto.setCreatedAt(recipe.getCreatedAt());
        dto.setUpdatedAt(recipe.getUpdatedAt());

        Set<String> categories = recipe.getCategories()
                .stream()
                .map(RecipeCategory::getName)
                .collect(Collectors.toSet());
        dto.setCategories(categories);

        List<RecipeResponseDto.IngredientInfo> ingList = recipe.getIngredients()
                .stream()
                .map(ri -> new RecipeResponseDto.IngredientInfo(
                        // ri.getId(),
                        ri.getIngredient() != null ? ri.getIngredient().getId() : null,
                        ri.getIngredient() != null ? ri.getIngredient().getName() : null,
                        ri.getQuantity(),
                        ri.getUnit()
                ))
                .collect(Collectors.toList());
        dto.setIngredients(ingList);
        dto.setCalories(recipe.getCalories());

        return dto;
    }

    @Override
    public RecipeResponseDto getRecipeById(Long id, CurrentUser currentUser) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        // ADMIN được xem mọi recipe
        if (currentUser.isAdmin()) {
            return convertToDto(recipe);
        }

        // USER: Được xem nếu recipe là PUBLISHED
        if (recipe.getStatus() == RecipeStatus.PUBLISHED) {
            return convertToDto(recipe);
        }

        // USER: Được xem nếu là recipe của mình (kể cả DRAFT)
        if (recipe.getCreatedBy() != null && recipe.getCreatedBy().getUserId().equals(currentUser.getId())) {
            return convertToDto(recipe);
        }

        // Nếu không rơi vào các trường hợp trên -> FORBIDDEN
        throw new AppException(ErrorCode.FORBIDDEN, "You do not have permission to view this recipe");
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id, CurrentUser currentUser) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        boolean isOwner = recipe.getCreatedBy() != null && recipe.getCreatedBy().getUserId().equals(currentUser.getId());

        if (currentUser.isUser() && !isOwner) {
            throw new AppException(ErrorCode.FORBIDDEN, "Bạn không có quyền xóa công thức này");
        }
        recipeRepository.delete(recipe);
    }

    @Override
    @Transactional
    public int deleteRecipesByIds(List<Long> idsQuery, DeleteRecipesDto body) {
        List<Long> idsToDelete;
        if (body != null && body.getIds() != null && !body.getIds().isEmpty()) {
            idsToDelete = body.getIds();
        } else if (idsQuery != null && !idsQuery.isEmpty()) {
            idsToDelete = idsQuery;
        } else {
            idsToDelete = Collections.emptyList();
        }
        if (idsQuery == null || idsQuery.isEmpty()) {
            return 0;
        }

        // find all existing recipes matching provided ids
        List<Recipe> existing = recipeRepository.findAllById(idsToDelete);

        if (existing.isEmpty()) {
            return 0;
        }

        // delete all found recipes in a single transaction to ensure integrity
        recipeRepository.deleteAll(existing);

        return existing.size();
    }

    @Override
    public Page<RecipeShortResponse> getRecipes(CurrentUser currentUser, RecipeStatus status, String category,
            String mealType, Integer minCookingTimeMinutes, Integer maxCookingTimeMinutes,
            BigDecimal minCalories, BigDecimal maxCalories,
            Integer page, Integer size, String sortBy, String sortDir, String keyword) {

        List<Long> ingredientIds = Collections.emptyList();

        String sortField = (sortBy == null || sortBy.isBlank()) ? "title" : sortBy.trim();

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        int pageSize = (size == null || size <= 0) ? 10 : size;
        int pageIndex = (page == null || page < 1) ? 0 : page - 1;

        PageRequest pageable = PageRequest.of(pageIndex, pageSize, Sort.by(direction, sortField));

        // ===== Base filters =====
        Specification<Recipe> spec = RecipeSpecifications.isPublicOnlyForUser(currentUser)
                .and(RecipeSpecifications.hasStatus(status))
                .and(RecipeSpecifications.hasCategory(category))
                .and(RecipeSpecifications.hasMealType(mealType))
                .and(RecipeSpecifications.cookingTimeBetween(minCookingTimeMinutes, maxCookingTimeMinutes))
                .and(RecipeSpecifications.caloriesBetween(minCalories, maxCalories));

        // ===== Keyword search (title/description) =====
        Specification<Recipe> keywordTextSpec = RecipeSpecifications.keywordLike(keyword);

        // ===== Keyword search (ingredient name → ingredientIds → recipe.ingredients)
        // =====
        Specification<Recipe> ingredientSpec = null;

        if (keyword != null && !keyword.isBlank()) {

            List<Ingredient> ingredients = ingredientRepository.findTop20ByNameContainingIgnoreCase(keyword.trim());

            ingredientIds = ingredients.stream()
                    .map(Ingredient::getId)
                    .toList();

            if (!ingredientIds.isEmpty()) {
                ingredientSpec = RecipeSpecifications.hasAnyIngredientIds(ingredientIds);
            }
        }

        // ===== Merge search spec =====
        if (keywordTextSpec != null && ingredientSpec != null) {
            spec = spec.and(keywordTextSpec.or(ingredientSpec));
        } else if (keywordTextSpec != null) {
            spec = spec.and(keywordTextSpec);
        } else if (ingredientSpec != null) {
            spec = spec.and(ingredientSpec);
        }

        Page<Recipe> result = recipeRepository.findAll(spec, pageable);

        // map đúng kiểu Page
        return result.map(r -> new RecipeShortResponse(
                r.getRecipeId(),
                r.getTitle(),
                r.getImageUrl(),
                r.getStatus(),
                r.getCategories().stream()
                        .map(RecipeCategory::getName)
                        .collect(Collectors.toSet()),
                r.getCookingTimeMinutes(),
                r.getCalories()));
    }

    @Override
    public RecipeResponseDto updateRecipeStatus(Long id, UpdateRecipeStatus status, CurrentUser currentUser) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECIPE_NOT_FOUND));

        boolean isOwner = recipe.getCreatedBy() != null && recipe.getCreatedBy().getUserId().equals(currentUser.getId());

        RecipeStatus targetStatus = status != null ? status.getStatus() : null;
        if (targetStatus == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Status is required");
        }

        if (currentUser.isUser()) {
            if (!isOwner) {
                throw new AppException(ErrorCode.FORBIDDEN, "You do not have permission to update the status of this recipe");
            }
            if (recipe.getStatus() == RecipeStatus.DRAFT && targetStatus != RecipeStatus.PENDING) {
                throw new AppException(ErrorCode.FORBIDDEN, "User can only change status from DRAFT to PENDING");
            }
            if (recipe.getStatus() == RecipeStatus.PENDING && targetStatus != RecipeStatus.DRAFT) {
                throw new AppException(ErrorCode.FORBIDDEN, "User can only change status from PENDING to DRAFT");
            }
            if (recipe.getStatus() != RecipeStatus.DRAFT && recipe.getStatus() != RecipeStatus.PENDING) {
                throw new AppException(ErrorCode.FORBIDDEN, "User cannot change status from the current state");
            }
        } else if (currentUser.isAdmin()) {
            if (recipe.getStatus() != RecipeStatus.PENDING) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "Admin can only change status when recipe is PENDING");
            }
            if (targetStatus != RecipeStatus.DRAFT && targetStatus != RecipeStatus.PUBLISHED) {
                throw new AppException(ErrorCode.VALIDATION_ERROR, "Admin can only set status to DRAFT or PUBLISHED");
            }

            if (targetStatus == RecipeStatus.PUBLISHED) {
                boolean exists = recipeRepository.existsByTitleAndStatus(recipe.getTitle(), RecipeStatus.PUBLISHED);
                if (exists) {
                    throw new AppException(ErrorCode.RECIPE_TITLE_ALREADY_EXISTS);
                }
            }
        } else {
            throw new AppException(ErrorCode.FORBIDDEN, "Unsupported role");
        }

        recipe.setStatus(targetStatus);
        recipe = recipeRepository.save(recipe);

        return convertToDto(recipe);
    }
}