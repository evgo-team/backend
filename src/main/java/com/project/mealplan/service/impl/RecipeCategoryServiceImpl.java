package com.project.mealplan.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.dtos.recipeCategory.request.createCategoryRequest;
import com.project.mealplan.dtos.recipeCategory.request.updateCategoryRequest;
import com.project.mealplan.dtos.recipeCategory.response.RecipeCategoryDto;
import com.project.mealplan.dtos.recipeCategory.response.RecipeCategoryDtoConverter;
import com.project.mealplan.entity.RecipeCategory;
import com.project.mealplan.repository.RecipeCategoryRepository;
import com.project.mealplan.service.RecipeCategoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeCategoryServiceImpl implements RecipeCategoryService {
	private final RecipeCategoryRepository recipeCategoryRepository;
    private final RecipeCategoryDtoConverter converter;

    @Override
    public List<RecipeCategoryDto> getAllCategories(){
        return converter.convert(recipeCategoryRepository.findAll());
    }

    @Override
    @Transactional
    public RecipeCategoryDto createCategory(createCategoryRequest request) {
        String name = request.getName().trim();

        if (recipeCategoryRepository.existsByName(name)) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        RecipeCategory category = new RecipeCategory();
        category.setName(name);
        RecipeCategory savedCategory = recipeCategoryRepository.save(category);
        return converter.convert(savedCategory);
    }

    @Override
    @Transactional
    public RecipeCategoryDto updateCategory(updateCategoryRequest request) {
        RecipeCategory category = recipeCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String newName = request.getNewCategoryName().trim();

        if (newName.equalsIgnoreCase(category.getName())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Không có thay đổi nào để lưu.");
            
        }

        if (recipeCategoryRepository.existsByName(newName)) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        category.setName(newName);
        RecipeCategory updatedCategory = recipeCategoryRepository.save(category);
        return converter.convert(updatedCategory);
    }


    @Override
    @Transactional
    public void deleteRecipeCategory(Long id) {
        RecipeCategory category = recipeCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        boolean isUsed = recipeCategoryRepository.isCategoryUsedInRecipes(id);
        if (isUsed) {
            throw new AppException(ErrorCode.CATEGORY_IN_USE, 
                    "Không thể xóa vì category này đang được sử dụng trong một hoặc nhiều công thức.");
        }

        recipeCategoryRepository.delete(category);
    }
    
}
