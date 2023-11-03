package com.security.gas.plant.service;

import com.security.gas.plant.dto.CompanyRequestDto;
import com.security.gas.plant.dto.CompanyResponseDto;
import com.security.gas.plant.dtomapper.CompanyDtoMapper;
import com.security.gas.plant.entity.Company;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.CompanyRepository;
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
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyResponseDto addNewCompany(CompanyRequestDto companyRequestDto, OrganizationRepository organizationRepository) throws MainServiceBusinessException {
        CompanyResponseDto companyResponseDto;
        try {
            String name = companyRequestDto.getName();
            if (companyRepository.existsCompanyByName(name)) {
                throw new DuplicateResourceException("Company with name [%s] already exists".formatted(name));
            }
            log.info("CompanyService:addNewCompany execution started.");
            Company company = CompanyDtoMapper.convertToEntity(companyRequestDto, organizationRepository);
            log.debug("CompanyService:addNewCompany request parameters {}", CompanyDtoMapper.jsonAsString(companyRequestDto));
            Company companyResult = companyRepository.save(company);
            companyResponseDto = CompanyDtoMapper.convertToDto(companyResult);
            log.debug("CompanyService:addNewCompany received response from Database {}", CompanyDtoMapper.jsonAsString(companyResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while persisting new company entry to the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while adding new company data entry", ex);
        }
        log.info("CompanyService:addNewCompany execution ended.");
        return companyResponseDto;
    }

    @Cacheable(value = "companies", unless = "#result == null")
    public List<CompanyResponseDto> getAllCompanies() throws MainServiceBusinessException {
        List<CompanyResponseDto> companyResponseDtos;
        try {
            log.info("CompanyService:getAllCompanies execution started.");
            List<Company> companyList = companyRepository.findAll();
            if (!companyList.isEmpty()) {
                companyResponseDtos = companyList.stream()
                        .map(CompanyDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                companyResponseDtos = Collections.emptyList();
            }
            log.debug("CompanyService:getAllCompanies retrieving all companies data from the database  {}", CompanyDtoMapper.jsonAsString(companyResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all companies from the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all companies from the Database", ex);
        }
        log.info("CompanyService:getCompanies execution ended.");
        return companyResponseDtos;
    }

    @Cacheable(value = "companyById", key = "#id", unless = "#result == null")
    public CompanyResponseDto getCompanyById(long id) {
        CompanyResponseDto companyResponseDto;
        try {
            log.info("CompanyService:getCompanyById execution started.");
            Company company = companyRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Company with id " + id + " not found"));
            companyResponseDto = CompanyDtoMapper.convertToDto(company);
            log.debug("CompanyService:getCompanyById retrieving company from the database with id {} {}", id, CompanyDtoMapper.jsonAsString(companyResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving company with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching company from Database " + id, ex);
        }
        log.info("CompanyService:getCompanyById execution ended.");
        return companyResponseDto;
    }

    @Cacheable(value = "companyByName", key = "#name", unless = "#result == null")
    public CompanyResponseDto getCompanyByName(String name) {
        CompanyResponseDto companyResponseDto;
        try {
            log.info("CompanyService:getCompanyByName execution started.");
            Company company = companyRepository.findCompanyByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Company with name " + name + " not found"));
            companyResponseDto = CompanyDtoMapper.convertToDto(company);
            log.debug("CompanyService:getCompanyByName retrieving company from the database with name {} {}", name, CompanyDtoMapper.jsonAsString(companyResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Company not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving company with name {} from the database, Exception message {}", name, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching company from Database " + name, ex);
        }
        log.info("CompanyService:getCompanyByName execution ended.");
        return companyResponseDto;
    }

    @Caching(evict = {
            @CacheEvict(value = "companies", allEntries = true),
            @CacheEvict(value = "companyById", key = "#id"),
            @CacheEvict(value = "companyByName", key = "#companyRequestDto.name"),
    })
    public CompanyResponseDto updateCompanyById(Long id, CompanyRequestDto companyRequestDto, OrganizationRepository organizationRepository) throws MainServiceBusinessException {
        CompanyResponseDto companyResponseDto;
        try {
            Optional<Company> existingCompanyOptional = companyRepository.findById(id);
            if (existingCompanyOptional.isEmpty()) {
                throw new ResourceNotFoundException("Company with ID " + id + " not found");
            }
            Company existingCompany = existingCompanyOptional.get();
            String updatedName = companyRequestDto.getName();
            if (!updatedName.equals(existingCompany.getName()) && companyRepository.existsCompanyByName(updatedName)) {
                throw new DuplicateResourceException("Company with name " + updatedName + " already exists");
            }
            log.info("CompanyService:updateCompanyById execution started for ID {}", id);
            CompanyDtoMapper.updateEntity(existingCompany, companyRequestDto, organizationRepository);
            Company updatedCompany = companyRepository.save(existingCompany);
            companyResponseDto = CompanyDtoMapper.convertToDto(updatedCompany);
            log.debug("CompanyService:updateCompanyById received response from Database {}", CompanyDtoMapper.jsonAsString(companyResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Company not found: " + ex.getMessage());
            throw ex;
        } catch (DuplicateResourceException ex) {
            log.error("Duplicate resource: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating company entry in the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while updating company data entry", ex);
        }
        log.info("CompanyService:updateCompanyById execution ended for ID {}", id);
        return companyResponseDto;
    }

    public void deleteCompanyById(Long id) {
        try {
            if (!companyRepository.existsCompanyById(id)) {
                throw new ResourceNotFoundException("Company with id [%s] not found".formatted(id));
            }
            log.debug("CompanyService:deleteCompanyById execution started");
            companyRepository.deleteById(id);
            log.debug("CompanyService:deleteCompanyById deleted company data from the database with id {}", id);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting company with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while deleting company from the database " + id, ex);
        }
        log.info("CompanyService:deleteCompanyById execution ended");
    }
}