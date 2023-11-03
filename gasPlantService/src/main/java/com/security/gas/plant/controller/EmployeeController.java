package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.EmployeeRequestDto;
import com.security.gas.plant.dto.EmployeeResponseDto;
import com.security.gas.plant.dtomapper.EmployeeDtoMapper;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.service.EmployeeService;
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
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    private final EmployeeService employeeService;

    @PreAuthorize("hasAuthority('employee:read')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllEmployees() {
        try {
            List<EmployeeResponseDto> employees = employeeService.getAllEmployees();
            ApiResponse<List<EmployeeResponseDto>> responseDTO = ApiResponse.<List<EmployeeResponseDto>>builder()
                    .status(SUCCESS)
                    .results(employees)
                    .build();
            log.info("EmployeeController::getAllEmployees response {}", EmployeeDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("EmployeeController::getAllEmployees error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('employee:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getEmployeeById(@PathVariable long id) {
        log.info("EmployeeController::getEmployeeById by id {}", id);
        try {
            EmployeeResponseDto employeeResponseDto = employeeService.getEmployeeById(id);
            ApiResponse<EmployeeResponseDto> responseDTO = ApiResponse.<EmployeeResponseDto>builder()
                    .status(SUCCESS)
                    .results(employeeResponseDto)
                    .build();
            log.info("EmployeeController::getEmployeeById by id {} response {}", id, EmployeeDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("EmployeeController::getEmployeeById error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("EmployeeController::getEmployeeById error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('employee:read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getEmployeeByName(@PathVariable String name) {
        log.info("EmployeeController::getEmployeeByName by name {}", name);
        try {
            EmployeeResponseDto employeeResponseDto = employeeService.getEmployeeByName(name);
            ApiResponse<EmployeeResponseDto> responseDTO = ApiResponse.<EmployeeResponseDto>builder()
                    .status(SUCCESS)
                    .results(employeeResponseDto)
                    .build();
            log.info("EmployeeController::getEmployeeByName by name {} response {}", name, EmployeeDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("EmployeeController::getEmployeeByName error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("EmployeeController::getEmployeeByName error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('employee:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEmployeeById(@PathVariable Long id, @RequestBody @Valid EmployeeRequestDto employeeRequestDto) {
        log.info("EmployeeController::updateEmployeeById request for ID {}", id);
        try {
            EmployeeResponseDto employeeResponseDto = employeeService.updateEmployeeById(id, employeeRequestDto);
            ApiResponse<EmployeeResponseDto> responseDTO = ApiResponse.<EmployeeResponseDto>builder()
                    .status(SUCCESS)
                    .results(employeeResponseDto)
                    .build();
            log.info("EmployeeController::updateEmployeeById for ID {} response {}", id, EmployeeDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("EmployeeController::updateEmployeeById error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("EmployeeController::updateEmployeeById error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('employee:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEmployeeById(@PathVariable Long id) {
        log.info("EmployeeController::deleteEmployeeById for ID {}", id);
        try {
            employeeService.deleteEmployeeById(id);
            ApiResponse<String> responseDTO = ApiResponse.<String>builder()
                    .status(SUCCESS)
                    .results("Employee deleted successfully")
                    .build();
            log.info("EmployeeController::deleteEmployeeById for ID {} response {}", id, EmployeeDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("EmployeeController::deleteEmployeeById error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("EmployeeController::deleteEmployeeById error response {}", EmployeeDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}