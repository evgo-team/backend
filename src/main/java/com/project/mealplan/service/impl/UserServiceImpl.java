package com.project.mealplan.service.impl;

import com.project.mealplan.dtos.auth.request.UserRegistrationRequest;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.enums.UserStatus;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.entity.Role;
import com.project.mealplan.entity.User;
import com.project.mealplan.repository.UserRepository;
import com.project.mealplan.service.RoleService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.mealplan.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public void register(UserRegistrationRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 2) Hash mật khẩu
        String hash = passwordEncoder.encode(request.getPassword());

        Role defaultRole = roleService.getDefaultUserRole();
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);

        // 3) Lưu DB
        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPassword(hash);
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(roles);
        userRepository.save(user);

    }

}
