package com.security.gas.plant.service;

import com.security.gas.plant.dto.RoleRequestDto;
import com.security.gas.plant.dto.RoleResponseDto;
import com.security.gas.plant.dtomapper.RoleDtoMapper;
import com.security.gas.plant.entity.PermissionEntity;
import com.security.gas.plant.entity.RoleEntity;
import com.security.gas.plant.repository.PermissionRepository;
import com.security.gas.plant.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleResponseDto createRoleWithPermissions(RoleRequestDto requestDto) {
        if (roleRepository.findByName(requestDto.getRoleName()).isPresent()) {
            throw new IllegalArgumentException("Role with name [" + requestDto.getRoleName() + "] already exists");
        }
        Set<PermissionEntity> permissions = (requestDto.getPermissionNames() != null ? requestDto.getPermissionNames() : new HashSet<>())
                .stream()
                .map(permissionName -> {
                    return permissionRepository.findByName((String) permissionName)
                            .orElseGet(() -> {
                                PermissionEntity newPermission = new PermissionEntity();
                                newPermission.setName((String) permissionName);
                                return permissionRepository.save(newPermission);
                            });
                })
                .collect(Collectors.toSet());
        RoleEntity role = new RoleEntity(null, requestDto.getRoleName(), permissions);
        RoleEntity savedRole = roleRepository.save(role);
        return RoleDtoMapper.toRoleResponseDto(savedRole);
    }
}