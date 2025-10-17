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

    // ====== RECIPE ======
    RECIPE_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "Recipe not found");

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
