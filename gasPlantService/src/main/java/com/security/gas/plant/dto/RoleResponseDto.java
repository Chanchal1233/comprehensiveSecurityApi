package com.security.gas.plant.dto;

import lombok.Data;

import java.util.Set;

@Data
public class RoleResponseDto {
    private Long roleId;
    private String roleName;
    private Set<String> permissionNames;
}
