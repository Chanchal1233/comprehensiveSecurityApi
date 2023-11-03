package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.OrganizationRequestDto;
import com.security.gas.plant.dto.OrganizationResponseDto;
import com.security.gas.plant.dtomapper.OrganizationDtoMapper;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/organization")
public class OrganizationController {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    private final OrganizationService organizationService;

    @PreAuthorize("hasAuthority('organization:create')")
    @PostMapping
    public ResponseEntity<ApiResponse> addNewOrganization(@RequestBody @Valid OrganizationRequestDto organizationRequestDto) {
        log.info("OrganizationController::addNewOrganization request body {}", OrganizationDtoMapper.jsonAsString(organizationRequestDto));
        try {
            OrganizationResponseDto organizationResponseDto = organizationService.addNewOrganization(organizationRequestDto);
            ApiResponse<OrganizationResponseDto> responseDTO = ApiResponse.<OrganizationResponseDto>builder()
                    .status(SUCCESS)
                    .results(organizationResponseDto)
                    .build();
            log.info("OrganizationController::addNewOrganization response {}", OrganizationDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("OrganizationController::addNewOrganization error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('organization:read')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllOrganizations() {
        try {
            List<OrganizationResponseDto> organizations = organizationService.getAllOrganizations();
            ApiResponse<List<OrganizationResponseDto>> responseDTO = ApiResponse.<List<OrganizationResponseDto>>builder()
                    .status(SUCCESS)
                    .results(organizations)
                    .build();
            log.info("OrganizationController::getAllOrganizations response {}", OrganizationDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("OrganizationController::getAllOrganizations error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('organization:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getOrganizationById(@PathVariable long id) {
        log.info("OrganizationController::getOrganizationById by id {}", id);
        try {
            OrganizationResponseDto organizationResponseDto = organizationService.getOrganizationById(id);
            ApiResponse<OrganizationResponseDto> responseDTO = ApiResponse.<OrganizationResponseDto>builder()
                    .status(SUCCESS)
                    .results(organizationResponseDto)
                    .build();
            log.info("OrganizationController::getOrganizationById by id {} response {}", id, OrganizationDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("OrganizationController::getOrganizationById error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("OrganizationController::getOrganizationById error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('organization:read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getOrganizationByName(@PathVariable String name) {
        log.info("OrganizationController::getOrganizationByName by name {}", name);
        try {
            OrganizationResponseDto organizationResponseDto = organizationService.getOrganizationByName(name);
            ApiResponse<OrganizationResponseDto> responseDTO = ApiResponse.<OrganizationResponseDto>builder()
                    .status(SUCCESS)
                    .results(organizationResponseDto)
                    .build();
            log.info("OrganizationController::getOrganizationByName by name {} response {}", name, OrganizationDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("OrganizationController::getOrganizationByName error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("OrganizationController::getOrganizationByName error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('organization:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateOrganizationById(@PathVariable long id, @RequestBody OrganizationRequestDto organizationRequestDto) {
        log.info("OrganizationController::updateOrganizationById by id {}", id);
        try {
            OrganizationResponseDto organizationResponseDto = organizationService.updateOrganizationById(id, organizationRequestDto);
            ApiResponse<OrganizationResponseDto> responseDTO = ApiResponse.<OrganizationResponseDto>builder()
                    .status(SUCCESS)
                    .results(organizationResponseDto)
                    .build();
            log.info("OrganizationController::updateOrganizationById by id {} response {}", id, OrganizationDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("OrganizationController::updateOrganizationById error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("OrganizationController::updateOrganizationById error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('organization:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteOrganizationById(@PathVariable long id) {
        log.info("OrganizationController::deleteOrganizationById by id {}", id);
        try {
            organizationService.deleteOrganizationById(id);
            ApiResponse<String> responseDTO = ApiResponse.<String>builder()
                    .status(SUCCESS)
                    .results("Organization deleted successfully")
                    .build();
            log.info("OrganizationController::deleteOrganizationById by id {} response: Organization deleted successfully", id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("OrganizationController::deleteOrganizationById error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("OrganizationController::deleteOrganizationById error response {}", OrganizationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}