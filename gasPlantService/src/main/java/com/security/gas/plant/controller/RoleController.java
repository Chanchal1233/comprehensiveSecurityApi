package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.RoleRequestDto;
import com.security.gas.plant.dto.RoleResponseDto;
import com.security.gas.plant.dtomapper.RoleDtoMapper;
import com.security.gas.plant.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.security.gas.plant.controller.RegionController.ERROR;
import static com.security.gas.plant.controller.RegionController.SUCCESS;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasAuthority('role:create')")
    @PostMapping
    public ResponseEntity<ApiResponse> createRoleWithPermissions(@RequestBody @Valid RoleRequestDto roleRequestDto) {
        log.info("RoleController::createRoleWithPermissions request body {}", RoleDtoMapper.jsonAsString(roleRequestDto));
        try {
            RoleResponseDto roleResponseDto = roleService.createRoleWithPermissions(roleRequestDto);

            ApiResponse<RoleResponseDto> responseDTO = ApiResponse.<RoleResponseDto>builder()
                    .status(SUCCESS)
                    .results(roleResponseDto)
                    .build();
            log.info("RoleController::createRoleWithPermissions response {}", RoleDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RoleController::createRoleWithPermissions error response {}", RoleDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
