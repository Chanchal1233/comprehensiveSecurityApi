package com.security.gas.plant.service;

import com.security.gas.plant.dto.RegionRequestDto;
import com.security.gas.plant.dto.RegionResponseDto;
import com.security.gas.plant.dtomapper.RegionDtoMapper;
import com.security.gas.plant.entity.Region;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.CompanyRepository;
import com.security.gas.plant.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionResponseDto addNewRegion(RegionRequestDto regionRequestDto, CompanyRepository companyRepository) throws MainServiceBusinessException {
        RegionResponseDto regionResponseDto;
        try {
            String name = regionRequestDto.getName();
            if (regionRepository.existsRegionByName(name)) {
                throw new DuplicateResourceException("Region with name [%s] already exists".formatted(name));
            }
            log.info("RegionService:addNewRegion execution started.");
            Region region = RegionDtoMapper.convertToEntity(regionRequestDto, companyRepository);
            log.debug("RegionService:addNewRegion request parameters {}", RegionDtoMapper.jsonAsString(regionRequestDto));
            Region regionResult = regionRepository.save(region);
            regionResponseDto = RegionDtoMapper.convertToDto(regionResult);
            log.debug("RegionService:addNewRegion received response from Database {}", RegionDtoMapper.jsonAsString(regionRequestDto));
        } catch (Exception ex) {
            log.error("Exception occurred while persisting new region entry to the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while adding new region data entry", ex);
        }
        log.info("RegionService:addNewRegion execution ended.");
        return regionResponseDto;
    }

    @Cacheable(value = "regions", unless = "#result == null")
    public List<RegionResponseDto> getAllRegions() throws MainServiceBusinessException {
        List<RegionResponseDto> regionResponseDtos;
        try {
            log.info("RegionService:getAllRegions execution started.");
            List<Region> regionList = regionRepository.findAll();
            if (!regionList.isEmpty()) {
                regionResponseDtos = regionList.stream()
                        .map(RegionDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                regionResponseDtos = Collections.emptyList();
            }
            log.debug("RegionService:getAllRegions retrieving all regions data from the database  {}", RegionDtoMapper.jsonAsString(regionResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all regions from the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all regions from the Database", ex);
        }
        log.info("RegionService:getRegions execution ended.");
        return regionResponseDtos;
    }

    @Cacheable(value = "regionById", key = "#id", unless = "#result == null")
    public RegionResponseDto getRegionById(long id) {
        RegionResponseDto regionResponseDto;
        try {
            log.info("RegionService:getRegionById execution started.");
            Region region = regionRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Region with id " + id + " not found"));
            regionResponseDto = RegionDtoMapper.convertToDto(region);
            log.debug("RegionService:getRegionById retrieving region from the database with id {} {}", id, RegionDtoMapper.jsonAsString(regionResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving region with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching region from Database " + id, ex);
        }
        log.info("RegionService:getRegionById execution ended.");
        return regionResponseDto;
    }

    @Cacheable(value = "regionByName", key = "#name", unless = "#result == null")
    public RegionResponseDto getRegionByName(String name) {
        RegionResponseDto regionResponseDto;
        try {
            log.info("RegionService:getRegionByName execution started.");
            Region region = regionRepository.findRegionByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Region with name " + name + " not found"));
            regionResponseDto = RegionDtoMapper.convertToDto(region);
            log.debug("RegionService:getRegionByName retrieving region from the database with name {} {}", name, RegionDtoMapper.jsonAsString(regionResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Region not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving region with name {} from the database, Exception message {}", name, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching region from Database " + name, ex);
        }
        log.info("RegionService:getRegionByName execution ended.");
        return regionResponseDto;
    }

    @Cacheable(value = "regionsByCompanyId", key = "#companyId", unless = "#result == null")
    public List<RegionResponseDto> getAllRegionsByOrganizationId(Long companyId) {
        List<RegionResponseDto> regionResponseDtos;
        try {
            log.info("RegionService:getAllRegionsByOrganizationId execution started.");
            List<Region> regionList = regionRepository.findAllByCompanyId(companyId);
            if (!regionList.isEmpty()) {
                regionResponseDtos = regionList.stream()
                        .map(RegionDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                regionResponseDtos = Collections.emptyList();
            }
            log.debug("RegionService:getAllRegionsByOrganizationId retrieving all regions data from database  {}", RegionDtoMapper.jsonAsString(regionResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all regions by organization ID from database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all regions by organization ID from the Database", ex);
        }
        log.info("RegionService:getAllRegionsByOrganizationId execution ended.");
        return regionResponseDtos;
    }

    @Caching(evict = {
            @CacheEvict(value = "regions", allEntries = true),
            @CacheEvict(value = "regionById", key = "#id"),
            @CacheEvict(value = "regionByName", key = "#regionRequestDto.name"),
            @CacheEvict(value = "regionsByCompanyId", key = "#regionRequestDto.companyId")
    })
    public RegionResponseDto updateRegionById(Long id, RegionRequestDto regionRequestDto, CompanyRepository organizationRepository) throws MainServiceBusinessException {
        RegionResponseDto regionResponseDto;
        try {
            Optional<Region> existingRegionOptional = regionRepository.findById(id);
            if (existingRegionOptional.isEmpty()) {
                throw new ResourceNotFoundException("Region with ID " + id + " not found");
            }
            Region existingRegion = existingRegionOptional.get();
            String updatedName = regionRequestDto.getName();
            if (!updatedName.equals(existingRegion.getName()) && regionRepository.existsRegionByName(updatedName)) {
                throw new DuplicateResourceException("Region with name " + updatedName + " already exists");
            }
            log.info("RegionService:updateRegionById execution started for ID {}", id);
            RegionDtoMapper.updateEntity(existingRegion, regionRequestDto, organizationRepository);
            Region updatedRegion = regionRepository.save(existingRegion);
            regionResponseDto = RegionDtoMapper.convertToDto(updatedRegion);
            log.debug("RegionService:updateRegionById received response from Database {}", RegionDtoMapper.jsonAsString(regionResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Region not found: " + ex.getMessage());
            throw ex;
        } catch (DuplicateResourceException ex) {
            log.error("Duplicate resource: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating region entry in the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while updating region data entry", ex);
        }
        log.info("RegionService:updateRegionById execution ended for ID {}", id);
        return regionResponseDto;
    }

    public void deleteRegionById(Long id) {
        try {
            if (!regionRepository.existsRegionById(id)) {
                throw new ResourceNotFoundException("Region with id [%s] not found".formatted(id));
            }
            log.debug("RegionService:deleteRegionById execution started");
            regionRepository.deleteById(id);
            log.debug("RegionService:deleteRegionById deleted region data from the database with id {}", id);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting region with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while deleting region from the database " + id, ex);
        }
        log.info("RegionService:deleteRegionById execution ended");
    }
}