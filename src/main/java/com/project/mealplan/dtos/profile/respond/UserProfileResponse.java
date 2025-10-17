package com.project.mealplan.dtos.profile.respond;

import com.project.mealplan.common.enums.DietType;
import com.project.mealplan.common.enums.Gender;
import com.project.mealplan.common.enums.HealthCondition;
import com.project.mealplan.common.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class UserProfileResponse {
    private Long userId;
    private String email;
    private UserStatus status;
    private Set<String> roles;

    // Profile fields
    private String fullName;
    private Integer age;
    private BigDecimal weight;
    private BigDecimal height;
    private Gender gender;
    private String bio;
    private String activityLevel;
    private HealthCondition healthCondition;
    private DietType dietType;
    private String profilePicUrl;
}