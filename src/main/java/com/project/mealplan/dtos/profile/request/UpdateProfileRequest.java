package com.project.mealplan.dtos.profile.request;

import com.project.mealplan.common.enums.ActivityLevel;
import com.project.mealplan.common.enums.DietType;
import com.project.mealplan.common.enums.Gender;
import com.project.mealplan.common.enums.HealthCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProfileRequest {
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @Min(value = 1, message = "Age must be greater than 0")
    private Integer age;

    @DecimalMin(value = "0.1", message = "Weight must be greater than 0")
    private BigDecimal weight;

    @DecimalMin(value = "0.1", message = "Height must be greater than 0")
    private BigDecimal height;

    private Gender gender;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    private ActivityLevel activityLevel;

    private HealthCondition healthCondition;

    private DietType dietType;

    private String profilePicUrl;
}