package com.security.gas.plant.dto;

import com.security.gas.plant.requests.registrationrequest.UserRegisterRequest;
import lombok.Data;

@Data
public class DatabaseInitializationRequestDto {
    private String accessCode;
    private OrganizationRequestDto organizationRequest;
    private CompanyRequestDto companyRequest;
    private RegionRequestDto regionRequest;
    private UserRegisterRequest userRegisterRequest;
}