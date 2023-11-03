package com.security.gas.plant.dto;

import lombok.Data;

@Data
public class DatabaseInitializationResponseDto {
    private OrganizationResponseDto organizationResponse;
    private CompanyResponseDto companyResponse;
    private RegionResponseDto regionResponse;
}

