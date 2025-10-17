package com.project.mealplan.service.impl;

import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.enums.IngredientType;
import com.project.mealplan.dtos.admin.BulkDeleteIngredientDto;
import com.project.mealplan.dtos.admin.BulkDeleteIngredientResponseDto;
import com.project.mealplan.dtos.admin.IngredientResponseDto;
import com.project.mealplan.dtos.admin.UpdateIngredientDto;
import com.project.mealplan.dtos.ingredient.request.IngredientCreateRequest;
import com.project.mealplan.dtos.ingredient.response.IngredientListItemResponse;
import com.project.mealplan.dtos.ingredient.response.IngredientResponse;
import com.project.mealplan.dtos.ingredient.response.IngredientResponseConverter;
import com.project.mealplan.entity.Ingredient;
import com.project.mealplan.entity.IngredientNutrition;
import com.project.mealplan.entity.NutritionType;
import com.project.mealplan.repository.IngredientRepository;
import com.project.mealplan.repository.NutritionTypeRepository;
import com.project.mealplan.repository.spec.IngredientSpecifications;
import com.project.mealplan.service.IngredientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

        private final IngredientRepository ingredientRepository;
        private final NutritionTypeRepository nutritionTypeRepository;
        private final IngredientResponseConverter ingredientResponseConverter;

        @Override
        @Transactional
        public IngredientResponse createIngredient(IngredientCreateRequest req) {
                String name = req.name().trim();

                // Check duplicate name
                if (ingredientRepository.existsByNameIgnoreCase(name)) {
                        throw new AppException(ErrorCode.INGREDIENT_NAME_ALREADY_EXISTS);
                }

                // Check Validate IngredientType (enum)
                IngredientType type;
                try {
                        type = IngredientType.valueOf(req.type().trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                        throw new AppException(ErrorCode.INVALID_INGREDIENT_TYPE);
                }

                // Build Ingredient entity
                Ingredient ingredient = new Ingredient();
                ingredient.setName(name);
                ingredient.setType(type);

                // For each nutrition entry: validate nutrition type exists and amount > 0
                Set<IngredientNutrition> nutritionSet = new HashSet<>();
                for (IngredientCreateRequest.NutritionDto n : req.nutritions()) {
                        NutritionType nutritionType = nutritionTypeRepository
                                        .findByNameIgnoreCase(n.nutritionName().trim())
                                        .orElseThrow(() -> new AppException(ErrorCode.NUTRITION_TYPE_NOT_FOUND,
                                                        "Nutrition type not found: " + n.nutritionName()));

                        if (n.amountPer100g().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                                throw new AppException(ErrorCode.INVALID_NUTRITION_AMOUNT,
                                                "Amount must be greater than 0 for: " + n.nutritionName());
                        }

                        IngredientNutrition in = new IngredientNutrition();
                        in.setIngredient(ingredient);
                        in.setNutritionType(nutritionType);
                        in.setAmountPer100g(n.amountPer100g());

                        nutritionSet.add(in);
                }

                ingredient.setNutritions(nutritionSet);
                Ingredient saved = ingredientRepository.save(ingredient);

                IngredientResponse ingredientResponse = ingredientResponseConverter.convert(saved);

                return ingredientResponse;
        }

        @Override
        @Transactional
        public IngredientResponseDto updateIngredient(Long id, UpdateIngredientDto updateDto) {
                // Find ingredient
                Ingredient ingredient = ingredientRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND));

                // Validate name uniqueness if name is changed
                if (!ingredient.getName().equals(updateDto.getName())) {
                        validateIngredientNameUniqueness(updateDto.getName(), id);
                }

                // Update basic info
                ingredient.setName(updateDto.getName());
                ingredient.setType(updateDto.getType());

                // Update nutritions safely to avoid ConcurrentModificationException
                updateIngredientNutritions(ingredient, updateDto.getNutritions());

                // Save and return
                Ingredient savedIngredient = ingredientRepository.save(ingredient);
                log.info("Successfully updated ingredient with id: {}", id);

                return convertToResponseDto(savedIngredient);
        }

        private void validateIngredientNameUniqueness(String name, Long excludeId) {
                boolean exists = ingredientRepository.existsByNameAndIdNot(name, excludeId);
                if (exists) {
                        throw new AppException(ErrorCode.INGREDIENT_NAME_ALREADY_EXISTS,
                                        "Tên ingredient '" + name + "' đã tồn tại");
                }
        }

        private void validateNutritionAmount(Double amount) {
                if (amount == null || amount <= 0) {
                        throw new AppException(ErrorCode.INVALID_NUTRITION_AMOUNT,
                                        "Lượng dinh dưỡng phải lớn hơn 0");
                }
        }

        private void updateIngredientNutritions(Ingredient ingredient,
                        List<UpdateIngredientDto.IngredientNutritionDto> nutritionDtos) {

                // Get existing nutritions as a Map for easy lookup
                Map<Long, IngredientNutrition> existingNutritions = ingredient.getNutritions().stream()
                                .collect(Collectors.toMap(
                                                nutrition -> nutrition.getNutritionType().getId(),
                                                nutrition -> nutrition));

                // New nutritions collection to replace the old one
                Set<IngredientNutrition> updatedNutritions = new HashSet<>();

                for (int i = 0; i < nutritionDtos.size(); i++) {
                        UpdateIngredientDto.IngredientNutritionDto nutritionDto = nutritionDtos.get(i);
                        Long nutritionTypeId = nutritionDto.getNutritionTypeId();
                        BigDecimal newAmount = BigDecimal.valueOf(nutritionDto.getAmountPer100g());

                        // Validate nutrition amount
                        validateNutritionAmount(nutritionDto.getAmountPer100g());

                        // Validate nutrition type exists in system (ALWAYS check this first!)
                        NutritionType nutritionType = nutritionTypeRepository
                                        .findById(nutritionTypeId)
                                        .orElseThrow(() -> new AppException(ErrorCode.NUTRITION_TYPE_NOT_FOUND,
                                                        "Nutrition type không tồn tại với ID: " + nutritionTypeId));

                        // Check if this nutrition type already exists for this ingredient
                        if (existingNutritions.containsKey(nutritionTypeId)) {
                                // UPDATE existing nutrition
                                IngredientNutrition existingNutrition = existingNutritions.get(nutritionTypeId);
                                existingNutrition.setAmountPer100g(newAmount);
                                updatedNutritions.add(existingNutrition);
                        } else {
                                // ADD new nutrition
                                IngredientNutrition newNutrition = new IngredientNutrition();
                                newNutrition.setIngredient(ingredient);
                                newNutrition.setNutritionType(nutritionType);
                                newNutrition.setAmountPer100g(newAmount);
                                updatedNutritions.add(newNutrition);
                        }
                }

                // Replace all nutritions with updated ones
                // This will remove nutritions not in the request and add new ones
                ingredient.getNutritions().clear();
                ingredient.getNutritions().addAll(updatedNutritions);

        }

        private IngredientResponseDto convertToResponseDto(Ingredient ingredient) {
                IngredientResponseDto responseDto = new IngredientResponseDto();
                responseDto.setId(ingredient.getId());
                responseDto.setName(ingredient.getName());
                responseDto.setType(ingredient.getType());

                List<IngredientResponseDto.NutritionInfo> nutritionInfos = ingredient.getNutritions().stream()
                                .map(this::convertToNutritionInfo)
                                .collect(Collectors.toList());

                responseDto.setNutritions(nutritionInfos);
                return responseDto;
        }

        private IngredientResponseDto.NutritionInfo convertToNutritionInfo(IngredientNutrition ingredientNutrition) {
                IngredientResponseDto.NutritionInfo nutritionInfo = new IngredientResponseDto.NutritionInfo();
                nutritionInfo.setNutritionTypeId(ingredientNutrition.getNutritionType().getId());
                nutritionInfo.setNutritionTypeName(ingredientNutrition.getNutritionType().getName());
                nutritionInfo.setUnit(ingredientNutrition.getNutritionType().getUnit().name());
                nutritionInfo.setAmountPer100g(ingredientNutrition.getAmountPer100g().doubleValue());
                return nutritionInfo;
        }

        @Override
        @Transactional
        public void deleteIngredient(Long id) {
                log.info("Deleting ingredient with id: {}", id);

                // Check if ingredient exists
                Ingredient ingredient = ingredientRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND,
                                                "Ingredient không tồn tại với ID: " + id));

                // Delete related nutrition data first (cascade will handle this, but explicit
                // for clarity)
                if (!ingredient.getNutritions().isEmpty()) {
                        ingredient.getNutritions().clear();
                        ingredientRepository.save(ingredient); // Save to trigger cascade delete
                }

                // Delete the ingredient
                ingredientRepository.delete(ingredient);
        }

        @Override
        @Transactional
        public BulkDeleteIngredientResponseDto bulkDeleteIngredients(BulkDeleteIngredientDto bulkDeleteDto) {

                List<Long> requestedIds = bulkDeleteDto.getIds();
                List<Long> deletedIds = new ArrayList<>();
                List<Long> notFoundIds = new ArrayList<>();

                // Remove duplicates while preserving order
                List<Long> uniqueIds = requestedIds.stream()
                                .distinct()
                                .collect(Collectors.toList());

                // Process each ID
                for (Long id : uniqueIds) {
                        try {
                                deleteIngredient(id);
                                deletedIds.add(id);
                        } catch (Exception e) {
                                notFoundIds.add(id);
                        }
                }

                // Build response
                List<String> errors = new ArrayList<>();
                if (!notFoundIds.isEmpty()) {
                        errors.add("Ingredients not found: " + notFoundIds);
                }

                BulkDeleteIngredientResponseDto response = new BulkDeleteIngredientResponseDto();
                response.setTotalRequested(requestedIds.size());
                response.setSuccessfullyDeleted(deletedIds.size());
                response.setFailed(notFoundIds.size());
                response.setFailedIds(notFoundIds);
                response.setErrors(errors);

                return response;
        }

        @Override
        @Transactional(readOnly = true)
        public IngredientResponse getIngredientDetailById(Long id) {
                return ingredientRepository.findWithNutritionsById(id)
                                .map(ingredientResponseConverter::convert)
                                .orElseThrow(() -> new AppException(ErrorCode.INGREDIENT_NOT_FOUND,
                                                "Ingredient not found with id " + id));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<IngredientListItemResponse> searchIngredients(
                        String type,
                        BigDecimal minCalories,
                        BigDecimal maxCalories,
                        String keyword,
                        int page,
                        int size,
                        String sortBy,
                        String sortDir) {
                Sort.Direction direction = ("desc".equalsIgnoreCase(sortDir)) ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(Math.max(page, 0), size > 0 ? size : 10,
                                Sort.by(direction, sortBy));

                Specification<Ingredient> spec = IngredientSpecifications.hasTypeIgnoreCase(type)
                                .and(IngredientSpecifications.hasKeyword(keyword))
                                .and(IngredientSpecifications.caloriesBetween(minCalories, maxCalories));

                Page<Ingredient> pageData = ingredientRepository.findAll(spec, pageable);

                return pageData.map(i -> new IngredientListItemResponse(
                                i.getId(),
                                i.getName().toString(),
                                i.getType().toString()));
        }

}