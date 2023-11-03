package com.security.gas.plant.facade;

import com.security.gas.plant.dao.UserDao;
import com.security.gas.plant.dto.*;
import com.security.gas.plant.dtomapper.PermissionDtoMapper;
import com.security.gas.plant.entity.*;
import com.security.gas.plant.repository.*;
import com.security.gas.plant.requests.registrationrequest.UserRegisterRequest;
import com.security.gas.plant.service.DistributorService;
import com.security.gas.plant.service.OrganizationService;
import com.security.gas.plant.service.RegionService;
import com.security.gas.plant.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserFacade {
    private static final String ACCESS_CODE = "4354583475738475394573457892729754928375237472384";
    private final UserService userService;
    private final OrganizationService organizationService;
    private final DistributorService distributorService;
    private final RegionService regionService;
    private final UserDao userDao;
    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;
    private final RegionRepository regionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;

    public UserDetailsDto getUserDetailsById(Integer userId) {
        User user = userService.getUserById(userId);
        RoleEntity userRole = user.getRole();
        String roleName = (userRole != null) ? userRole.getName() : null;
        UserDetailsDto userDetailsDto = new UserDetailsDto(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                roleName,
                null,
                null,
                null
        );
        if (user.getCompany() != null) {
            userDetailsDto.setAssociation("Organization");
            OrganizationResponseDto organization = organizationService.getOrganizationById(user.getCompany().getId());
            userDetailsDto.setAssociationDetails(organization);
        } else if (user.getDistributor() != null) {
            userDetailsDto.setAssociation("Distributor");
            DistributorResponseDto distributor = distributorService.getDistributorById(user.getDistributor().getId());
            userDetailsDto.setAssociationDetails(distributor);
            if (distributor.getRegionId() != null) {
                userDetailsDto.setAssociation("Distributor with Region");
                RegionResponseDto region = regionService.getRegionById(distributor.getRegionId());
                userDetailsDto.setRegionDetails(region);
            }
        }
        return userDetailsDto;
    }

    @Transactional
    public DatabaseInitializationResponseDto launchInitialDatabaseSetup(DatabaseInitializationRequestDto requestDto) {
        if (userRepository.existsAnyUser() || organizationRepository.existsAny() || companyRepository.existsAny() || regionRepository.existsAny()) {
            throw new IllegalStateException("Database is not empty. Initialization aborted.");
        }

        if (!ACCESS_CODE.equals(requestDto.getAccessCode())) {
            throw new IllegalArgumentException("Invalid access code provided.");
        }

        List<String> entities = Arrays.asList("user", "company", "distributor", "employee", "organization", "permission", "region", "role");
        for (String entity : entities) {
            createPermission(new PermissionRequestDto(entity + ":create"));
            createPermission(new PermissionRequestDto(entity + ":read"));
            createPermission(new PermissionRequestDto(entity + ":update"));
            createPermission(new PermissionRequestDto(entity + ":delete"));
        }

        Organization organization = new Organization();
        organization.setName(requestDto.getOrganizationRequest().getName());
        organization.setReg(requestDto.getOrganizationRequest().getReg());
        organization.setIndustry(requestDto.getOrganizationRequest().getIndustry());
        organization.setLocation(requestDto.getOrganizationRequest().getLocation());
        organization.setContact(requestDto.getOrganizationRequest().getContact());
        organization = organizationRepository.save(organization);

        Company company = new Company();
        company.setName(requestDto.getCompanyRequest().getName());
        company.setAddress(requestDto.getCompanyRequest().getAddress());
        company.setOrganization(organization);
        company = companyRepository.save(company);

        Region region = new Region();
        region.setName(requestDto.getRegionRequest().getName());
        region.setCompany(company);
        region = regionRepository.save(region);

        RoleEntity superAdminRole = roleRepository.findByName("SUPER-ADMIN")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("SUPER-ADMIN");
                    return roleRepository.save(role);
                });

        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setFirstname(requestDto.getUserRegisterRequest().getFirstname());
        userRegisterRequest.setLastname(requestDto.getUserRegisterRequest().getLastname());
        userRegisterRequest.setEmail(requestDto.getUserRegisterRequest().getEmail());
        userRegisterRequest.setPassword(requestDto.getUserRegisterRequest().getPassword());
        userRegisterRequest.setCompanyId(company.getId());
        userRegisterRequest.setRole(superAdminRole.getName());

        userService.register(userRegisterRequest);

        DatabaseInitializationResponseDto response = new DatabaseInitializationResponseDto();

        OrganizationResponseDto organizationResponse = new OrganizationResponseDto();
        organizationResponse.setId(organization.getId());
        organizationResponse.setName(organization.getName());
        organizationResponse.setReg(organization.getReg());
        organizationResponse.setIndustry(organization.getIndustry());
        organizationResponse.setLocation(organization.getLocation());
        organizationResponse.setContact(organization.getContact());

        CompanyResponseDto companyResponse = new CompanyResponseDto();
        companyResponse.setId(company.getId());
        companyResponse.setName(company.getName());
        companyResponse.setAddress(company.getAddress());
        companyResponse.setOrganizationId(organization.getId());

        RegionResponseDto regionResponse = new RegionResponseDto();
        regionResponse.setId(region.getId());
        regionResponse.setName(region.getName());
        regionResponse.setCompanyId(company.getId());

        response.setOrganizationResponse(organizationResponse);
        response.setCompanyResponse(companyResponse);
        response.setRegionResponse(regionResponse);

        return response;
    }

    public PermissionResponseDto createPermission(PermissionRequestDto permissionRequestDto) {
        PermissionEntity permission = new PermissionEntity(null, permissionRequestDto.getName());
        PermissionEntity savedPermission = permissionRepository.save(permission);
        return PermissionDtoMapper.toPermissionResponseDto(savedPermission);
    }
}