package com.project.mealplan.service.impl;

import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.exception.AppException;
import com.project.mealplan.entity.Role;
import com.project.mealplan.repository.RoleRepository;
import org.springframework.stereotype.Service;

import com.project.mealplan.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
    }

    @Override
    public Role getDefaultUserRole() {
        return findByName("USER");
    }
}