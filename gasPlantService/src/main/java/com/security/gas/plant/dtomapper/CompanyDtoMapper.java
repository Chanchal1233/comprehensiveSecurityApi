package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.CompanyRequestDto;
import com.security.gas.plant.dto.CompanyResponseDto;
import com.security.gas.plant.entity.Company;
import com.security.gas.plant.entity.Organization;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompanyDtoMapper {

    public static Company convertToEntity(CompanyRequestDto companyRequestDto, OrganizationRepository organizationRepository) {
        Company company = new Company();
        company.setName(companyRequestDto.getName());
        company.setAddress(companyRequestDto.getAddress());

        Long organizationId = companyRequestDto.getOrganizationId();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization with ID " + organizationId + " not found"));
        company.setOrganization(organization);
        return company;
    }

    public static CompanyResponseDto convertToDto(Company company) {
        CompanyResponseDto companyResponseDto = new CompanyResponseDto();
        companyResponseDto.setId(company.getId());
        companyResponseDto.setName(company.getName());
        companyResponseDto.setAddress(company.getAddress());

        if (company.getOrganization() != null) {
            companyResponseDto.setOrganizationId(company.getOrganization().getId());
        }
        return companyResponseDto;
    }

    public static void updateEntity(Company existingCompany, CompanyRequestDto companyRequestDto, OrganizationRepository organizationRepository) {
        existingCompany.setName(companyRequestDto.getName());
        existingCompany.setAddress(companyRequestDto.getAddress());

        Long organizationId = companyRequestDto.getOrganizationId();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization with ID " + organizationId + " not found"));
        existingCompany.setOrganization(organization);
    }

    public static String jsonAsString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

