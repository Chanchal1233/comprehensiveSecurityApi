package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.RoleResponseDto;
import com.security.gas.plant.entity.PermissionEntity;
import com.security.gas.plant.entity.RoleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

public class RoleDtoMapper {

    private static final Logger log = LoggerFactory.getLogger(RoleDtoMapper.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String jsonAsString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON string: {}", e.getMessage());
            return null;
        }
    }

    public static RoleResponseDto toRoleResponseDto(RoleEntity roleEntity) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setRoleId(roleEntity.getId());
        dto.setRoleName(roleEntity.getName());
        dto.setPermissionNames(
                roleEntity.getPermissions().stream()
                        .map(PermissionEntity::getName)
                        .collect(Collectors.toSet())
        );
        return dto;
    }
}

