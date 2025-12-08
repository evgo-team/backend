package com.project.mealplan.security;

import java.util.Set;

public record CurrentUser(Long id, Set<String> roles) {
    public boolean isAdmin() { return roles.contains("ROLE_ADMIN"); }
    public boolean isUser()  { return roles.contains("ROLE_USER");  }
    public Long getId() { return id; }
}