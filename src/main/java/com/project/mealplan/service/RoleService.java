package com.project.mealplan.service;

import com.project.mealplan.entity.Role;

public interface RoleService {
    Role findByName(String name);
    Role getDefaultUserRole();
}