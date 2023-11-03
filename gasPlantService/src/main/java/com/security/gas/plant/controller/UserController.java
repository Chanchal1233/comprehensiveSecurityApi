package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.UserDto;
import com.security.gas.plant.facade.UserFacade;
import com.security.gas.plant.requests.registrationrequest.UserRegisterRequest;
import com.security.gas.plant.requests.updaterequest.UserUpdateRequest;
import com.security.gas.plant.security.userauthentication.UserAuthenticationRequest;
import com.security.gas.plant.security.userauthentication.UserAuthenticationResponse;
import com.security.gas.plant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    public static final String SUCCESS = "Success";
    private final UserService service;
    private final UserFacade userFacade;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<ApiResponse<UserAuthenticationResponse>> register(
            @RequestBody UserRegisterRequest request
    ) {
        try {
            UserAuthenticationResponse response = service.register(request);
            ApiResponse<UserAuthenticationResponse> apiResponse = ApiResponse
                    .<UserAuthenticationResponse>builder()
                    .status(SUCCESS)
                    .results(response)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred during user registration: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<ApiResponse<UserAuthenticationResponse>> authenticate(
            @RequestBody UserAuthenticationRequest request
    ) {
        try {
            UserAuthenticationResponse response = service.authenticate(request);
            ApiResponse<UserAuthenticationResponse> apiResponse = ApiResponse
                    .<UserAuthenticationResponse>builder()
                    .status(SUCCESS)
                    .results(response)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred during user authentication: {}", ex.getMessage());
            throw ex;
        }
    }

    @PostMapping("/api/v1/auth/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            service.refreshToken(request, response);
        } catch (Exception ex) {
            log.error("Exception occurred during token refresh: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/api/v1/admin/get/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUser() {
        try {
            List<UserDto> users = service.getAllUsers();
            ApiResponse<List<UserDto>> apiResponse = ApiResponse
                    .<List<UserDto>>builder()
                    .status(SUCCESS)
                    .results(users)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching all users: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/api/v1/auth/get/user/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable("id") Integer id) {
        try {
            UserDto userDto = service.getUser(id);
            ApiResponse<UserDto> apiResponse = ApiResponse
                    .<UserDto>builder()
                    .status(SUCCESS)
                    .results(userDto)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching user by ID: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/api/v1/admin/get/users/organization/{id}")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByOrganizationId(@PathVariable("id") Long companyId) {
        try {
            List<UserDto> users = service.getUsersByCompanyId(companyId);
            ApiResponse<List<UserDto>> apiResponse = ApiResponse
                    .<List<UserDto>>builder()
                    .status(SUCCESS)
                    .results(users)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users by Organization ID: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/api/v1/admin/get/users/distributor/{id}")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByDistributorId(@PathVariable("id") Long distributorId) {
        try {
            List<UserDto> users = service.getUsersByDistributorId(distributorId);
            ApiResponse<List<UserDto>> apiResponse = ApiResponse
                    .<List<UserDto>>builder()
                    .status(SUCCESS)
                    .results(users)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users by Distributor ID: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/api/v1/admin/get/users/with-organization")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersWithOrganization() {
        try {
            List<UserDto> users = service.getUsersWithCompany();
            ApiResponse<List<UserDto>> apiResponse = ApiResponse
                    .<List<UserDto>>builder()
                    .status(SUCCESS)
                    .results(users)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users with Organization: {}", ex.getMessage());
            throw ex;
        }
    }

    @GetMapping("/api/v1/admin/get/users/with-distributor")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersWithDistributor() {
        try {
            List<UserDto> users = service.getUsersWithDistributor();
            ApiResponse<List<UserDto>> apiResponse = ApiResponse
                    .<List<UserDto>>builder()
                    .status(SUCCESS)
                    .results(users)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users with Distributor: {}", ex.getMessage());
            throw ex;
        }
    }

        @DeleteMapping("/api/v1/admin/delete/user/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Integer id) {
        try {
            service.deleteUserById(id);
            ApiResponse<Void> apiResponse = ApiResponse
                    .<Void>builder()
                    .status(SUCCESS)
                    .results(null)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting user by ID: {}", ex.getMessage());
            throw ex;
        }
    }

    @PutMapping("/api/v1/admin/update/user/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable("id") Integer id, @RequestBody UserUpdateRequest updateRequest) {
        try {
            service.updateUser(id, updateRequest);
            ApiResponse<Void> apiResponse = ApiResponse
                    .<Void>builder()
                    .status(SUCCESS)
                    .results(null)
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            log.error("Exception occurred while updating user by ID: {}", ex.getMessage());
            throw ex;
        }
    }
}