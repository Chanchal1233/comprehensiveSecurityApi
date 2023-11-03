package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.OrganizationRequestDto;
import com.security.gas.plant.dto.OrganizationResponseDto;
import com.security.gas.plant.entity.Organization;

public class OrganizationDtoMapper {

    public static Organization convertToEntity(OrganizationRequestDto organizationRequestDto) {
        Organization organization = new Organization();
        organization.setName(organizationRequestDto.getName());
        organization.setReg(organizationRequestDto.getReg());
        organization.setIndustry(organizationRequestDto.getIndustry());
        organization.setLocation(organizationRequestDto.getLocation());
        organization.setContact(organizationRequestDto.getContact());
        return organization;
    }

    public static OrganizationResponseDto convertToDto(Organization organization) {
        OrganizationResponseDto organizationResponseDto = new OrganizationResponseDto();
        organizationResponseDto.setId(organization.getId());
        organizationResponseDto.setName(organization.getName());
        organizationResponseDto.setReg(organization.getReg());
        organizationResponseDto.setIndustry(organization.getIndustry());
        organizationResponseDto.setLocation(organization.getLocation());
        organizationResponseDto.setContact(organization.getContact());
        return organizationResponseDto;
    }

    public static void updateEntity(Organization existingOrganization, OrganizationRequestDto organizationRequestDto) {
        existingOrganization.setName(organizationRequestDto.getName());
        existingOrganization.setReg(organizationRequestDto.getReg());
        existingOrganization.setIndustry(organizationRequestDto.getIndustry());
        existingOrganization.setLocation(organizationRequestDto.getLocation());
        existingOrganization.setContact(organizationRequestDto.getContact());
    }

    public static String jsonAsString(Object obj){
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}