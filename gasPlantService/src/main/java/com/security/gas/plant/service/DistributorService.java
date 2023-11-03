package com.security.gas.plant.service;

import com.security.gas.plant.dto.DistributorRequestDto;
import com.security.gas.plant.dto.DistributorResponseDto;
import com.security.gas.plant.dtomapper.DistributorDtoMapper;
import com.security.gas.plant.entity.Distributor;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.DistributorRepository;
import com.security.gas.plant.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class DistributorService {

    private final DistributorRepository distributorRepository;
    private final RegionRepository regionRepository;
    private final CacheManager cacheManager;

    public DistributorResponseDto addNewDistributor(DistributorRequestDto distributorRequestDto) throws MainServiceBusinessException {
        DistributorResponseDto distributorResponseDto;
        try {
            String name = distributorRequestDto.getName();
            if (distributorRepository.existsDistributorByName(name)) {
                throw new DuplicateResourceException("Distributor with name [%s] already exists".formatted(name));
            }
            log.info("DistributorService:addNewDistributor execution started.");
            Distributor distributor = DistributorDtoMapper.convertToEntity(distributorRequestDto, regionRepository);
            log.debug("DistributorService:addNewDistributor request parameters {}", DistributorDtoMapper.jsonAsString(distributorRequestDto));
            Distributor distributorResult = distributorRepository.save(distributor);
            distributorResponseDto = DistributorDtoMapper.convertToDto(distributorResult);
            log.debug("DistributorService:addNewDistributor received response from Database {}", DistributorDtoMapper.jsonAsString(distributorRequestDto));
        } catch (Exception ex) {
            log.error("Exception occurred while persisting new distributor entry to the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while adding new distributor data entry", ex);
        }
        log.info("DistributorService:addNewDistributor execution ended.");
        return distributorResponseDto;
    }
    @Cacheable(value = "distributors", unless = "#result == null")
    public List<DistributorResponseDto> getAllDistributors() throws MainServiceBusinessException {
        List<DistributorResponseDto> distributorResponseDtos;
        try {
            log.info("DistributorService:getAllDistributors execution started.");
            List<Distributor> distributorList = distributorRepository.findAll();
            if (!distributorList.isEmpty()) {
                distributorResponseDtos = distributorList.stream()
                        .map(DistributorDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                distributorResponseDtos = Collections.emptyList();
            }
            log.debug("DistributorService:getAllDistributors retrieving all distributors data from the database  {}", DistributorDtoMapper.jsonAsString(distributorResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all distributors from the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all distributors from the Database", ex);
        }
        log.info("DistributorService:getDistributors execution ended.");
        return distributorResponseDtos;
    }

    @Cacheable(value = "distributorById", key = "#id", unless = "#result == null")
    public DistributorResponseDto getDistributorById(long id) {
        DistributorResponseDto distributorResponseDto;
        try {
            log.info("DistributorService:getDistributorById execution started.");
            Distributor distributor = distributorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Distributor with id " + id + " not found"));
            distributorResponseDto = DistributorDtoMapper.convertToDto(distributor);
            log.debug("DistributorService:getDistributorById retrieving distributor from the database with id {} {}", id, DistributorDtoMapper.jsonAsString(distributorResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving distributor with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching distributor from Database " + id, ex);
        }
        log.info("DistributorService:getDistributorById execution ended.");
        return distributorResponseDto;
    }

    @Cacheable(value = "distributorByName", key = "#name", unless = "#result == null")
    public DistributorResponseDto getDistributorByName(String name) {
        DistributorResponseDto distributorResponseDto;
        try {
            log.info("DistributorService:getDistributorByName execution started.");
            Distributor distributor = distributorRepository.findDistributorByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Distributor with name " + name + " not found"));
            distributorResponseDto = DistributorDtoMapper.convertToDto(distributor);
            log.debug("DistributorService:getDistributorByName retrieving distributor from the database with name {} {}", name, DistributorDtoMapper.jsonAsString(distributorResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Distributor not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving distributor with name {} from the database, Exception message {}", name, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching distributor from Database " + name, ex);
        }
        log.info("DistributorService:getDistributorByName execution ended.");
        return distributorResponseDto;
    }

    @Cacheable(value = "distributorByRegionId", key = "#regionId", unless = "#result == null")
    public List<DistributorResponseDto> getAllDistributorsByRegionId(Long regionId) {
        List<DistributorResponseDto> distributorResponseDtos;
        try {
            log.info("DistributorService:getAllDistributorsByRegionId execution started.");
            List<Distributor> distributorList = distributorRepository.findAllByRegionId(regionId);
            if (!distributorList.isEmpty()) {
                distributorResponseDtos = distributorList.stream()
                        .map(DistributorDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                distributorResponseDtos = Collections.emptyList();
            }
            log.debug("DistributorService:getAllDistributorsByRegionId retrieving all distributors data from database  {}", DistributorDtoMapper.jsonAsString(distributorResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all distributors by region ID from database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all distributors by region ID from the Database", ex);
        }
        log.info("DistributorService:getAllDistributorsByRegionId execution ended.");
        return distributorResponseDtos;
    }

    @Caching(evict = {
            @CacheEvict(value = "distributors", allEntries = true),
            @CacheEvict(value = "distributorById", key = "#id"),
            @CacheEvict(value = "distributorByName", key = "#distributorRequestDto.name"),
            @CacheEvict(value = "distributorByRegionId", key = "#distributorRequestDto.regionId")
    })
    public DistributorResponseDto updateDistributorById(Long id, DistributorRequestDto distributorRequestDto, RegionRepository regionRepository) throws MainServiceBusinessException {
        DistributorResponseDto distributorResponseDto;
        try {
            Optional<Distributor> existingDistributorOptional = distributorRepository.findById(id);
            if (existingDistributorOptional.isEmpty()) {
                throw new ResourceNotFoundException("Distributor with ID " + id + " not found");
            }
            Distributor existingDistributor = existingDistributorOptional.get();
            String updatedName = distributorRequestDto.getName();
            if (!updatedName.equals(existingDistributor.getName()) && distributorRepository.existsDistributorByName(updatedName)) {
                throw new DuplicateResourceException("Distributor with name " + updatedName + " already exists");
            }
            log.info("DistributorService:updateDistributorById execution started for ID {}", id);
            DistributorDtoMapper.updateEntity(existingDistributor, distributorRequestDto, regionRepository);
            Distributor updatedDistributor = distributorRepository.save(existingDistributor);
            distributorResponseDto = DistributorDtoMapper.convertToDto(updatedDistributor);
            log.debug("DistributorService:updateDistributorById received response from Database {}", DistributorDtoMapper.jsonAsString(distributorResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Distributor not found: " + ex.getMessage());
            throw ex;
        } catch (DuplicateResourceException ex) {
            log.error("Duplicate resource: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating distributor entry in the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while updating distributor data entry", ex);
        }
        log.info("DistributorService:updateDistributorById execution ended for ID {}", id);
        return distributorResponseDto;
    }

    public void deleteDistributorById(Long id) {
        try {
            if (!distributorRepository.existsDistributorById(id)) {
                throw new ResourceNotFoundException("Distributor with id [%s] not found".formatted(id));
            }
            log.debug("DistributorService:deleteDistributorById execution started");
            distributorRepository.deleteById(id);
            log.debug("DistributorService:deleteDistributorById deleted distributor data from the database with id {}", id);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting distributor with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while deleting distributor from the database " + id, ex);
        }
        log.info("DistributorService:deleteDistributorById execution ended");
    }
}