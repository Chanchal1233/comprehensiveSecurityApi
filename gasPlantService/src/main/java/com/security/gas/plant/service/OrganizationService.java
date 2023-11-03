package com.security.gas.plant.service;

import com.security.gas.plant.dto.OrganizationRequestDto;
import com.security.gas.plant.dto.OrganizationResponseDto;
import com.security.gas.plant.dtomapper.OrganizationDtoMapper;
import com.security.gas.plant.entity.Organization;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.OrganizationRepository;
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
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationResponseDto addNewOrganization(OrganizationRequestDto organizationRequestDto) throws MainServiceBusinessException {
        OrganizationResponseDto organizationResponseDto;
        try {
            String name = organizationRequestDto.getName();
            if(organizationRepository.existsOrganizationByName(name))
                throw new DuplicateResourceException("Organization with name [%s] already exists".formatted(name));
            log.info("OrganizationService:addNewOrganization execution started.");
            Organization organization = OrganizationDtoMapper.convertToEntity(organizationRequestDto);
            log.debug("OrganizationService:addNewOrganization request parameters {}", OrganizationDtoMapper.jsonAsString(organizationRequestDto));
            Organization organizationResult = organizationRepository.save(organization);
            organizationResponseDto = OrganizationDtoMapper.convertToDto(organizationResult);
            log.debug("OrganizationService:addNewOrganization received response from Database {}", OrganizationDtoMapper.jsonAsString(organizationRequestDto));
        } catch (Exception ex) {
            log.error("Exception occurred while persisting new organization entry to database , Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while adding new organization data entry", ex);
        }
        log.info("OrganizationService:addNewOrganization execution ended.");
        return organizationResponseDto;
    }

    @Cacheable(value = "organizations", unless = "#result == null")
    public List<OrganizationResponseDto> getAllOrganizations() throws MainServiceBusinessException {
        List<OrganizationResponseDto> organizationResponseDtos;
        try {
            log.info("OrganizationService:getAllOrganizations execution started.");
            List<Organization> organizationList = organizationRepository.findAll();
            if (!organizationList.isEmpty()) {
                organizationResponseDtos = organizationList.stream()
                        .map(OrganizationDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                organizationResponseDtos = Collections.emptyList();
            }
            log.debug("OrganizationService:getAllOrganizations retrieving all organizations data from database  {}", OrganizationDtoMapper.jsonAsString(organizationResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all organizations from database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all organizations from Database", ex);
        }
        log.info("OrganizationService:getOrganizations execution ended.");
        return organizationResponseDtos;
    }

     @Cacheable(value = "organizationById", key = "#id", unless = "#result == null" )
     public OrganizationResponseDto getOrganizationById(long id) {
        OrganizationResponseDto organizationResponseDto;
        try {
            log.info("OrganizationService:getOrganizationById execution started.");
            Organization organization = organizationRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Organization with id " + id + " not found"));
            organizationResponseDto = OrganizationDtoMapper.convertToDto(organization);
            log.debug("OrganizationService:getOrganizationById retrieving organization from database with id {} {}", id, OrganizationDtoMapper.jsonAsString(organizationResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving organization with id {} from database , Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching organization from Database " + id, ex);
        }
        log.info("OrganizationService:getOrganizationById execution ended.");
        return organizationResponseDto;
    }

    @Cacheable(value = "organizationByName", key = "#name", unless = "#result == null")
    public OrganizationResponseDto getOrganizationByName(String name) {
        OrganizationResponseDto organizationResponseDto;
        try {
            log.info("OrganizationService:getOrganizationByName execution started.");
            Organization organization = organizationRepository.findOrganizationByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Organization with name " + name + " not found"));
            organizationResponseDto = OrganizationDtoMapper.convertToDto(organization);
            log.debug("OrganizationService:getOrganizationByName retrieving organization from database with name {} {}", name, OrganizationDtoMapper.jsonAsString(organizationResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Organization not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving organization with name {} from the database, Exception message {}", name, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching organization from Database " + name, ex);
        }
        log.info("OrganizationService:getOrganizationByName execution ended.");
        return organizationResponseDto;
    }

    @Caching(evict = {
            @CacheEvict(value = "organizations", allEntries = true),
            @CacheEvict(value = "organizationById", key = "#id"),
            @CacheEvict(value = "organizationByName", key = "#organizationRequestDto.name"),
    })
    public OrganizationResponseDto updateOrganizationById(Long id, OrganizationRequestDto organizationRequestDto) throws MainServiceBusinessException {
        OrganizationResponseDto organizationResponseDto;
        try {
            Optional<Organization> existingOrganizationOptional = organizationRepository.findById(id);
            if (existingOrganizationOptional.isEmpty()) {
                throw new ResourceNotFoundException("Organization with ID " + id + " not found");
            }
            Organization existingOrganization = existingOrganizationOptional.get();
            String updatedName = organizationRequestDto.getName();
            if (!updatedName.equals(existingOrganization.getName()) && organizationRepository.existsOrganizationByName(updatedName)) {
                throw new DuplicateResourceException("Organization with name " + updatedName + " already exists");
            }
            log.info("OrganizationService:updateOrganizationById execution started for ID {}", id);
            OrganizationDtoMapper.updateEntity(existingOrganization, organizationRequestDto);
            Organization updatedOrganization = organizationRepository.save(existingOrganization);
            organizationResponseDto = OrganizationDtoMapper.convertToDto(updatedOrganization);
            log.debug("OrganizationService:updateOrganizationById received response from Database {}", OrganizationDtoMapper.jsonAsString(organizationResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Organization not found: " + ex.getMessage());
            throw ex;
        } catch (DuplicateResourceException ex) {
            log.error("Duplicate resource: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating organization entry in the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while updating organization data entry", ex);
        }
        log.info("OrganizationService:updateOrganizationById execution ended for ID {}", id);
        return organizationResponseDto;
    }

    public void deleteOrganizationById(Long id) {
        try{
            if (!organizationRepository.existsOrganizationById(id)) {
                throw new ResourceNotFoundException(
                        "Organization with id [%s] not found".formatted(id)
                );
            }
            log.debug("OrganizationService:deleteOrganizationById execution started");
            organizationRepository.deleteById(id);
            log.debug("OrganizationService:deleteOrganizationById deleted organization data from data with id");
        } catch (Exception ex) {
            log.error("Exception occurred while delete organization with id {} from database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while deleting organization from database " + id, ex);
        }
        log.info("OrganizationService:deleteOrganizationById execution ended");
    }
}