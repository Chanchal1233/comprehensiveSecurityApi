package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.DatabaseInitializationRequestDto;
import com.security.gas.plant.dto.DatabaseInitializationResponseDto;
import com.security.gas.plant.dto.InitializationDtoMapper;
import com.security.gas.plant.facade.UserFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.security.gas.plant.controller.RegionController.ERROR;
import static com.security.gas.plant.controller.RegionController.SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/initialization")
public class DatabaseInitializationController {

    private final UserFacade userFacade;


    @PostMapping("/launch")
    public ResponseEntity<ApiResponse> initializeDatabase(@RequestBody @Valid DatabaseInitializationRequestDto requestDto) {
        log.info("DatabaseInitializationController::initializeDatabase request body {}", InitializationDtoMapper.jsonAsString(requestDto));
        try {
            DatabaseInitializationResponseDto responseDto = userFacade.launchInitialDatabaseSetup(requestDto);
            ApiResponse<DatabaseInitializationResponseDto> response = ApiResponse.<DatabaseInitializationResponseDto>builder()
                    .status(SUCCESS)
                    .results(responseDto)
                    .build();
            log.info("DatabaseInitializationController::initializeDatabase response {}", InitializationDtoMapper.jsonAsString(response));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("DatabaseInitializationController::initializeDatabase error response {}", InitializationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("DatabaseInitializationController::initializeDatabase error response {}", InitializationDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}