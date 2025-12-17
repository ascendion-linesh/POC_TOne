package com.bookstore.domain.security;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {

    @Id
    private int roleId;

    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();

    // Default constructor
    public Role() {}

    // Parameterized constructor
    public Role(int roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    // --- Standard Getters and Setters (used by Spring JPA) ---

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    // Optional: descriptive method (not required but helpful)
    public String getRoleDescription() {
        return switch (name) {
            case "ROLE_ADMIN" -> "Administrator";
            case "ROLE_USER" -> "Regular User";
            default -> "Unknown Role";
        };
    }
}
