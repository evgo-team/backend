package com.project.mealplan.common.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST(400, HttpStatus.BAD_REQUEST, "Invalid request"),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(403, HttpStatus.FORBIDDEN, "Access denied"),
    NOT_FOUND(404, HttpStatus.NOT_FOUND, "Resource not found"),

    VALIDATION_ERROR(400, HttpStatus.BAD_REQUEST, "Validation failed"),
    INVALID_AGE(400, HttpStatus.BAD_REQUEST, "Age must be greater than 0"),
    INVALID_WEIGHT(400, HttpStatus.BAD_REQUEST, "Weight must be greater than 0"),
    INVALID_HEIGHT(400, HttpStatus.BAD_REQUEST, "Height must be greater than 0"),
    INVALID_GENDER(400, HttpStatus.BAD_REQUEST, "Gender must be valid"),
    INVALID_ACTIVITY_LEVEL(400, HttpStatus.BAD_REQUEST, "Activity level must be valid"),

    // ====== USER ======
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(1002, HttpStatus.CONFLICT, "User already exists"),

    // ====== INGREDIENT ======
    INGREDIENT_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "Ingredient not found"),
    INGREDIENT_NAME_ALREADY_EXISTS(2002, HttpStatus.CONFLICT, "Ingredient name already exists"),
    NUTRITION_TYPE_NOT_FOUND(2003, HttpStatus.NOT_FOUND, "Nutrition type not found"),
    INVALID_NUTRITION_AMOUNT(2004, HttpStatus.BAD_REQUEST, "Nutrition amount must be greater than 0"),
    INVALID_INGREDIENT_TYPE(2005, HttpStatus.BAD_REQUEST, "Invalid ingredient type"),
    INGREDIENT_IN_USE(2006, HttpStatus.CONFLICT, "Ingredient is being used and cannot be deleted"),
    INVALID_INGREDIENT_UNIT(2007, HttpStatus.BAD_REQUEST, "Invalid ingredient unit"),

    // ====== RECIPE ======
    RECIPE_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Recipe not found"),
    RECIPE_TITLE_ALREADY_EXISTS(3002, HttpStatus.CONFLICT, "Recipe title already exists"),
    CATEGORY_NOT_FOUND(3003, HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_ALREADY_EXISTS(3004, HttpStatus.NOT_FOUND, "Recipe category with the same name already exists."),
    CATEGORY_IN_USE(3005, HttpStatus.BAD_REQUEST, "Category is currently used in one or more recipes"),

    // ======= FAVORITE ======
    FAVORITE_NOT_FOUND(4001, HttpStatus.NOT_FOUND, "Favorite not found"),
    FAVORITE_ALREADY_EXISTS(4002, HttpStatus.CONFLICT, "Favorite already exists"),

    // ======== PANTRY ========
    PANTRY_ITEM_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "Pantry item not found"),
    UNAUTHORIZED_ACCESS_PANTRY(5002, HttpStatus.FORBIDDEN, "You do not have permission to access this pantry item"),

    // ====== MEAL PLAN ======
    MEAL_PLAN_NOT_FOUND(6001, HttpStatus.NOT_FOUND, "Meal plan not found"),
    MEAL_PLAN_ALREADY_EXISTS(6002, HttpStatus.CONFLICT, "Meal plan already exists for this week"),
    INVALID_DATE_FORMAT(6003, HttpStatus.BAD_REQUEST, "Invalid date format. Expected yyyy-MM-dd"),
    INSUFFICIENT_USER_PROFILE(6004, HttpStatus.BAD_REQUEST,
            "User profile incomplete. Weight, height, age required for meal planning"),
    NO_RECIPES_AVAILABLE(6005, HttpStatus.BAD_REQUEST, "No recipes available for meal planning");

    private final int code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
