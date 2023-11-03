package com.security.gas.plant.controller;

import com.security.gas.plant.dto.ApiResponse;
import com.security.gas.plant.dto.DistributorAndUsersInputDto;
import com.security.gas.plant.dto.DistributorRequestDto;
import com.security.gas.plant.dto.DistributorResponseDto;
import com.security.gas.plant.dtomapper.DistributorDtoMapper;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.facade.DistributorFacade;
import com.security.gas.plant.repository.RegionRepository;
import com.security.gas.plant.service.DistributorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/distributors")
public class DistributorController {

    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    private final DistributorService distributorService;
    private final RegionRepository regionRepository;
    private final DistributorFacade distributorFacade;
    private final CacheManager cacheManager;

    @PreAuthorize("hasAuthority('distributor:create')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createDistributorAndUsers(@Valid @RequestBody DistributorAndUsersInputDto distributorAndUsersInputDto) {
        log.info("DistributorController::createDistributorAndUsers request body {}", DistributorDtoMapper.jsonAsString(distributorAndUsersInputDto));
        try {
            distributorFacade.createDistributorAndUsers(distributorAndUsersInputDto);
            ApiResponse<String> responseDTO = ApiResponse
                    .<String>builder()
                    .status(SUCCESS)
                    .build();
            log.info("DistributorController::createDistributorAndUsers response {}", DistributorDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (DuplicateResourceException | IllegalArgumentException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::createDistributorAndUsers error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (MainServiceBusinessException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::createDistributorAndUsers error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:create')")
    @PostMapping
    public ResponseEntity<ApiResponse> addNewDistributor(@RequestBody @Valid DistributorRequestDto distributorRequestDto) {
        log.info("DistributorController::addNewDistributor request body {}", DistributorDtoMapper.jsonAsString(distributorRequestDto));
        try {
            DistributorResponseDto distributorResponseDto = distributorService.addNewDistributor(distributorRequestDto);
            ApiResponse<DistributorResponseDto> responseDTO = ApiResponse
                    .<DistributorResponseDto>builder()
                    .status(SUCCESS)
                    .results(distributorResponseDto)
                    .build();
            log.info("DistributorController::addNewDistributor response {}", DistributorDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (DuplicateResourceException | IllegalArgumentException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::addNewDistributor error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (MainServiceBusinessException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::addNewDistributor error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::addNewDistributor error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:read')")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllDistributors() {
        log.info("DistributorController::getAllDistributors request initiated");
        try {
            List<DistributorResponseDto> distributors = distributorService.getAllDistributors();
            ApiResponse<List<DistributorResponseDto>> responseDTO = ApiResponse
                    .<List<DistributorResponseDto>>builder()
                    .status(SUCCESS)
                    .results(distributors)
                    .build();
            log.info("DistributorController::getAllDistributors response {}", DistributorDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::getAllDistributors error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::getAllDistributors error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getDistributorById(@PathVariable long id) {
        log.info("DistributorController::getDistributorById by id  {}", id);
        try {
            DistributorResponseDto distributorResponseDto = distributorService.getDistributorById(id);
            ApiResponse<DistributorResponseDto> responseDTO = ApiResponse
                    .<DistributorResponseDto>builder()
                    .status(SUCCESS)
                    .results(distributorResponseDto)
                    .build();
            log.info("DistributorController::getDistributor by id  {} response {}", id, DistributorDtoMapper.jsonAsString(distributorResponseDto));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::getDistributorById error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::getDistributorById error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:read')")
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse> getDistributorByName(@PathVariable String name) {
        log.info("DistributorController::getDistributorByName by name {}", name);
        try {
            DistributorResponseDto distributorResponseDto = distributorService.getDistributorByName(name);

            ApiResponse<DistributorResponseDto> responseDTO = ApiResponse
                    .<DistributorResponseDto>builder()
                    .status(SUCCESS)
                    .results(distributorResponseDto)
                    .build();
            log.info("DistributorController::getDistributorByName by name {} response {}", name, DistributorDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::getDistributorByName error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::getDistributorByName error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:read')")
    @GetMapping("/region/{regionId}")
    public ResponseEntity<ApiResponse> getAllDistributorsByRegionId(@PathVariable long regionId) {
        log.info("DistributorController::getAllDistributorsByRegionId by regionId {}", regionId);
        try {
            List<DistributorResponseDto> distributors = distributorService.getAllDistributorsByRegionId(regionId);
            ApiResponse<List<DistributorResponseDto>> responseDTO = ApiResponse
                    .<List<DistributorResponseDto>>builder()
                    .status(SUCCESS)
                    .results(distributors)
                    .build();
            log.info("DistributorController::getAllDistributorsByRegionId by regionId {} response {}", regionId, DistributorDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::getAllDistributorsByRegionId error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::getAllDistributorsByRegionId error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:update')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateDistributorById(@PathVariable long id, @RequestBody DistributorRequestDto distributorRequestDto) {
        log.info("DistributorController::updateDistributorById by id {}", id);
        try {
            DistributorResponseDto distributorResponseDto = distributorService.updateDistributorById(id, distributorRequestDto, regionRepository);
            ApiResponse<DistributorResponseDto> responseDTO = ApiResponse
                    .<DistributorResponseDto>builder()
                    .status(SUCCESS)
                    .results(distributorResponseDto)
                    .build();
            log.info("DistributorController::updateDistributorById by id {} response {}", id, DistributorDtoMapper.jsonAsString(responseDTO));
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException | IllegalArgumentException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::updateDistributorById error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::updateDistributorById error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('distributor:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteDistributorById(@PathVariable long id) {
        log.info("DistributorController::deleteDistributorById by id  {}", id);
        try {
            distributorService.deleteDistributorById(id);
            ApiResponse<String> responseDTO = ApiResponse
                    .<String>builder()
                    .status(SUCCESS)
                    .results("Distributor deleted successfully")
                    .build();
            log.info("DistributorController::deleteDistributorById by id  {} response: Distributor deleted successfully", id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results(ex.getMessage())
                    .build();
            log.error("DistributorController::deleteDistributorById error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            ApiResponse<String> errorResponse = ApiResponse
                    .<String>builder()
                    .status(ERROR)
                    .results("An unexpected error occurred")
                    .build();
            log.error("DistributorController::deleteDistributorById error response {}", DistributorDtoMapper.jsonAsString(errorResponse));
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}