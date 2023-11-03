package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.CompanyRequestDto;
import com.security.gas.plant.dto.CompanyResponseDto;
import com.security.gas.plant.dtomapper.CompanyDtoMapper;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.OrganizationRepository;
import com.security.gas.plant.service.CompanyService;
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
@RequestMapping("/api/v1/company")
public class CompanyController {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    private final CompanyService companyService;
    private final OrganizationRepository organizationRepository;

    @PreAuthorize("hasAuthority('company:create')")
    @PostMapping
    public ResponseEntity<ApiResponse> addNewCompany(@RequestBody @Valid CompanyRequestDto companyRequestDto) {
        log.info("CompanyController::addNewCompany request body {}", CompanyDtoMapper.jsonAsString(companyRequestDto));
        try {
            CompanyResponseDto companyResponseDto = companyService.addNewCompany(companyRequestDto, organizationRepository);
            ApiResponse<CompanyResponseDto> responseDTO = ApiResponse.<CompanyResponseDto>builder()
                    .status(SUCCESS)
                    .results(companyResponseDto)
                    .build();
            log.info("CompanyController::addNewCompany response {}", CompanyDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("CompanyController::addNewCompany error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("CompanyController::addNewCompany error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('company:read')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCompanies() {
        try {
            List<CompanyResponseDto> companies = companyService.getAllCompanies();
            ApiResponse<List<CompanyResponseDto>> responseDTO = ApiResponse.<List<CompanyResponseDto>>builder()
                    .status(SUCCESS)
                    .results(companies)
                    .build();
            log.info("CompanyController::getAllCompanies response {}", CompanyDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("CompanyController::getAllCompanies error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('company:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCompanyById(@PathVariable long id) {
        log.info("CompanyController::getCompanyById by id {}", id);
        try {
            CompanyResponseDto companyResponseDto = companyService.getCompanyById(id);
            ApiResponse<CompanyResponseDto> responseDTO = ApiResponse.<CompanyResponseDto>builder()
                    .status(SUCCESS)
                    .results(companyResponseDto)
                    .build();
            log.info("CompanyController::getCompanyById by id {} response {}", id, CompanyDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("CompanyController::getCompanyById error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("CompanyController::getCompanyById error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('company:read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getCompanyByName(@PathVariable String name) {
        log.info("CompanyController::getCompanyByName by name {}", name);
        try {
            CompanyResponseDto companyResponseDto = companyService.getCompanyByName(name);
            ApiResponse<CompanyResponseDto> responseDTO = ApiResponse.<CompanyResponseDto>builder()
                    .status(SUCCESS)
                    .results(companyResponseDto)
                    .build();
            log.info("CompanyController::getCompanyByName by name {} response {}", name, CompanyDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("CompanyController::getCompanyByName error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("CompanyController::getCompanyByName error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('company:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCompanyById(@PathVariable long id, @RequestBody CompanyRequestDto companyRequestDto) {
        log.info("CompanyController::updateCompanyById by id {}", id);
        try {
            CompanyResponseDto companyResponseDto = companyService.updateCompanyById(id, companyRequestDto, organizationRepository);
            ApiResponse<CompanyResponseDto> responseDTO = ApiResponse.<CompanyResponseDto>builder()
                    .status(SUCCESS)
                    .results(companyResponseDto)
                    .build();
            log.info("CompanyController::updateCompanyById by id {} response {}", id, CompanyDtoMapper.jsonAsString(companyResponseDto));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("CompanyController::updateCompanyById error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("CompanyController::updateCompanyById error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('company:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCompanyById(@PathVariable long id) {
        log.info("CompanyController::deleteCompanyById by id {}", id);
        try {
            companyService.deleteCompanyById(id);
            ApiResponse<String> responseDTO = ApiResponse.<String>builder()
                    .status(SUCCESS)
                    .results("Company deleted successfully")
                    .build();
            log.info("CompanyController::deleteCompanyById by id {} response: Company deleted successfully", id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("CompanyController::deleteCompanyById error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("CompanyController::deleteCompanyById error response {}", CompanyDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

