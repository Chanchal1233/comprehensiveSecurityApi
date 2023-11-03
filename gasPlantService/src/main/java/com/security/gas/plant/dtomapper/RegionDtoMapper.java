package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.RegionRequestDto;
import com.security.gas.plant.dto.RegionResponseDto;
import com.security.gas.plant.entity.Company;
import com.security.gas.plant.entity.Region;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.CompanyRepository;

public class RegionDtoMapper {

    public static Region convertToEntity(RegionRequestDto regionRequestDto, CompanyRepository companyRepository) {
        Region region = new Region();
        region.setName(regionRequestDto.getName());
        Long organizationId = regionRequestDto.getCompanyId();
        Company company = companyRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization with ID " + organizationId + " not found"));
        region.setCompany(company);
        return region;
    }

    public static RegionResponseDto convertToDto(Region region) {
        RegionResponseDto regionResponseDto = new RegionResponseDto();
        regionResponseDto.setId(region.getId());
        regionResponseDto.setName(region.getName());
        if (region.getCompany() != null) {
            regionResponseDto.setCompanyId(region.getCompany().getId());
        }
        return regionResponseDto;
    }

    public static void updateEntity(Region existingRegion, RegionRequestDto regionRequestDto, CompanyRepository companyRepository) {
        existingRegion.setName(regionRequestDto.getName());
        Long organizationId = regionRequestDto.getCompanyId();
        Company company = companyRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization with ID " + organizationId + " not found"));
        existingRegion.setCompany(company);
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