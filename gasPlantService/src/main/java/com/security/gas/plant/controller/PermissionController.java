package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.PermissionRequestDto;
import com.security.gas.plant.dto.PermissionResponseDto;
import com.security.gas.plant.dtomapper.PermissionDtoMapper;
import com.security.gas.plant.service.PermissionService;
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

import static com.security.gas.plant.controller.EmployeeController.ERROR;
import static com.security.gas.plant.controller.EmployeeController.SUCCESS;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/permission")
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasAuthority('permission:create')")
    @PostMapping
    public ResponseEntity<ApiResponse> createPermission(@RequestBody @Valid PermissionRequestDto permissionRequestDto) {
        log.info("PermissionController::createPermission request body {}", PermissionDtoMapper.jsonAsString(permissionRequestDto));
        try {
            PermissionResponseDto permissionResponseDto = permissionService.createPermission(permissionRequestDto);
            ApiResponse<PermissionResponseDto> responseDTO = ApiResponse.<PermissionResponseDto>builder()
                    .status(SUCCESS)
                    .results(permissionResponseDto)
                    .build();
            log.info("PermissionController::createPermission response {}", PermissionDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("Error occurred during permission creation: {}", ex.getMessage(), ex);
            log.error("PermissionController::createPermission error response {}", PermissionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}