package com.project.mealplan.service.impl;

import com.project.mealplan.dtos.profile.request.UpdateProfileRequest;
import com.project.mealplan.dtos.profile.respond.UserProfileResponse;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.entity.Role;
import com.project.mealplan.entity.User;
import com.project.mealplan.repository.UserRepository;
import com.project.mealplan.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
        private final UserRepository userRepository;

        @Override
        @Transactional
        public void updateProfile(Long userId, UpdateProfileRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                // Update all profile fields without null checks
                user.setFullName(request.getFullName());
                user.setAge(request.getAge());
                user.setWeight(request.getWeight());
                user.setHeight(request.getHeight());
                user.setGender(request.getGender());
                user.setBio(request.getBio());
                user.setActivityLevel(request.getActivityLevel());
                user.setHealthCondition(request.getHealthCondition());
                user.setDietType(request.getDietType());
                user.setProfilePicUrl(request.getProfilePicUrl());

                userRepository.save(user);
        }

        @Override
        public UserProfileResponse getProfile(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                return UserProfileResponse.builder()
                                .userId(user.getUserId())
                                .email(user.getEmail())
                                .status(user.getStatus())
                                .roles(user.getRoles() != null && !user.getRoles().isEmpty() ? user.getRoles().stream()
                                                .map(Role::getName)
                                                .collect(Collectors.toSet()) : null)
                                .fullName(user.getFullName())
                                .age(user.getAge())
                                .weight(user.getWeight())
                                .height(user.getHeight())
                                .gender(user.getGender())
                                .bio(user.getBio())
                                .activityLevel(user.getActivityLevel().name())
                                .healthCondition(user.getHealthCondition())
                                .dietType(user.getDietType())
                                .profilePicUrl(user.getProfilePicUrl())
                                .build();
        }
}