package com.security.gas.plant.service;

import com.security.gas.plant.dto.PermissionRequestDto;
import com.security.gas.plant.dto.PermissionResponseDto;
import com.security.gas.plant.dtomapper.PermissionDtoMapper;
import com.security.gas.plant.entity.PermissionEntity;
import com.security.gas.plant.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionResponseDto createPermission(PermissionRequestDto permissionRequestDto) {
        PermissionEntity permission = new PermissionEntity(null, permissionRequestDto.getName());
        PermissionEntity savedPermission = permissionRepository.save(permission);
        return PermissionDtoMapper.toPermissionResponseDto(savedPermission);
    }

}
