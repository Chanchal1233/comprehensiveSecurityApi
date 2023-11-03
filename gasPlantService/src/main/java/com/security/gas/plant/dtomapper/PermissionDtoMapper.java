package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.PermissionResponseDto;
import com.security.gas.plant.entity.PermissionEntity;

public class PermissionDtoMapper {

    public static PermissionResponseDto toPermissionResponseDto(PermissionEntity permissionEntity) {
        return new PermissionResponseDto(permissionEntity.getId(), permissionEntity.getName());
    }

    public static String jsonAsString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

