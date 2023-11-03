package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.RegionRequestDto;
import com.security.gas.plant.dto.RegionResponseDto;
import com.security.gas.plant.dtomapper.RegionDtoMapper;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.CompanyRepository;
import com.security.gas.plant.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/regions")
public class RegionController {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    private final RegionService regionService;
    private final CompanyRepository companyRepository;

    @PreAuthorize("hasAuthority('region:create')")
    @PostMapping
    public ResponseEntity<ApiResponse> addNewRegion(@RequestBody @Valid RegionRequestDto regionRequestDto) {
        log.info("RegionController::addNewRegion request body {}", RegionDtoMapper.jsonAsString(regionRequestDto));
        try {
            RegionResponseDto regionResponseDto = regionService.addNewRegion(regionRequestDto, companyRepository);
            ApiResponse<RegionResponseDto> responseDTO = ApiResponse.<RegionResponseDto>builder()
                    .status(SUCCESS)
                    .results(regionResponseDto)
                    .build();
            log.info("RegionController::addNewRegion response {}", RegionDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("RegionController::addNewRegion error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::addNewRegion error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('region:read')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRegions() {
        try {
            List<RegionResponseDto> regions = regionService.getAllRegions();
            ApiResponse<List<RegionResponseDto>> responseDTO = ApiResponse.<List<RegionResponseDto>>builder()
                    .status(SUCCESS)
                    .results(regions)
                    .build();
            log.info("RegionController::getAllRegions response {}", RegionDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::getAllRegions error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('region:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getRegionById(@PathVariable long id) {
        log.info("RegionController::getRegionById by id {}", id);
        try {
            RegionResponseDto regionResponseDto = regionService.getRegionById(id);
            ApiResponse<RegionResponseDto> responseDTO = ApiResponse.<RegionResponseDto>builder()
                    .status(SUCCESS)
                    .results(regionResponseDto)
                    .build();
            log.info("RegionController::getRegionById by id {} response {}", id, RegionDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("RegionController::getRegionById error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::getRegionById error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('region:read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getRegionByName(@PathVariable String name) {
        log.info("RegionController::getRegionByName by name {}", name);
        try {
            RegionResponseDto regionResponseDto = regionService.getRegionByName(name);
            ApiResponse<RegionResponseDto> responseDTO = ApiResponse.<RegionResponseDto>builder()
                    .status(SUCCESS)
                    .results(regionResponseDto)
                    .build();
            log.info("RegionController::getRegionByName by name {} response {}", name, RegionDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("RegionController::getRegionByName error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::getRegionByName error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('region:read')")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse> getAllRegionsByOrganizationId(@PathVariable long organizationId) {
        log.info("RegionController::getAllRegionsByOrganizationId by organizationId {}", organizationId);
        try {
            List<RegionResponseDto> regions = regionService.getAllRegionsByOrganizationId(organizationId);
            ApiResponse<List<RegionResponseDto>> responseDTO = ApiResponse.<List<RegionResponseDto>>builder()
                    .status(SUCCESS)
                    .results(regions)
                    .build();
            log.info("RegionController::getAllRegionsByOrganizationId by organizationId {} response {}", organizationId, RegionDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("RegionController::getAllRegionsByOrganizationId error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::getAllRegionsByOrganizationId error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('region:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRegionById(@PathVariable long id, @RequestBody RegionRequestDto regionRequestDto) {
        log.info("RegionController::updateRegionById by id {}", id);
        try {
            RegionResponseDto regionResponseDto = regionService.updateRegionById(id, regionRequestDto, companyRepository);
            ApiResponse<RegionResponseDto> responseDTO = ApiResponse.<RegionResponseDto>builder()
                    .status(SUCCESS)
                    .results(regionResponseDto)
                    .build();
            log.info("RegionController::updateRegionById by id {} response {}", id, RegionDtoMapper.jsonAsString(regionResponseDto));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("RegionController::updateRegionById error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::updateRegionById error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('region:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRegionById(@PathVariable long id) {
        log.info("RegionController::deleteRegionById by id {}", id);
        try {
            regionService.deleteRegionById(id);
            ApiResponse<String> responseDTO = ApiResponse.<String>builder()
                    .status(SUCCESS)
                    .results("Region deleted successfully")
                    .build();
            log.info("RegionController::deleteRegionById by id {} response: Region deleted successfully", id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("RegionController::deleteRegionById error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("RegionController::deleteRegionById error response {}", RegionDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}