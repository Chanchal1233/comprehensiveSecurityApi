package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.UserDataRequestDto;
import com.security.gas.plant.dto.UserDataResponseDto;
import com.security.gas.plant.dtomapper.UserDataDtoMapper;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.service.UserDataService;
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
@RequestMapping("/api/v1/user/data")
public class UserDataController {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";

    private final UserDataService userDataService;

    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUserData() {
        try {
            List<UserDataResponseDto> users = userDataService.getAllUserData();
            ApiResponse<List<UserDataResponseDto>> responseDTO = ApiResponse.<List<UserDataResponseDto>>builder()
                    .status(SUCCESS)
                    .results(users)
                    .build();
            log.info("UserController::getAllUserData response {}", UserDataDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (MainServiceBusinessException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("UserController::getAllUserData error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserDataById(@PathVariable long id) {
        try {
            UserDataResponseDto userData = userDataService.getUserDataById(id);
            ApiResponse<UserDataResponseDto> responseDTO = ApiResponse.<UserDataResponseDto>builder()
                    .status(SUCCESS)
                    .results(userData)
                    .build();
            log.info("UserController::getUserDataById response for id {} is {}", id, UserDataDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("UserController::getUserDataById error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("UserController::getUserDataById error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUserDataById(@PathVariable long id, @RequestBody UserDataRequestDto userDataRequestDto) {
        try {
            UserDataResponseDto updatedUserData = userDataService.updateUserDataById(id, userDataRequestDto);
            ApiResponse<UserDataResponseDto> responseDTO = ApiResponse.<UserDataResponseDto>builder()
                    .status(SUCCESS)
                    .results(updatedUserData)
                    .build();
            log.info("UserController::updateUserDataById response for id {} is {}", id, UserDataDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("UserController::updateUserDataById error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (MainServiceBusinessException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("UserController::updateUserDataById error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUserDataById(@PathVariable long id) {
        try {
            userDataService.deleteUserDataById(id);
            ApiResponse<String> responseDTO = ApiResponse.<String>builder()
                    .status(SUCCESS)
                    .results("User data deleted successfully")
                    .build();
            log.info("UserController::deleteUserDataById response: User data for id {} deleted successfully", id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results(ex.getMessage()).build();
            log.error("UserController::deleteUserDataById error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (MainServiceBusinessException ex) {
            ApiResponse<String> errorResponse = ApiResponse.<String>builder().status(ERROR).results("An unexpected error occurred").build();
            log.error("UserController::deleteUserDataById error response {}", UserDataDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}