package com.security.gas.plant.facade;

import com.security.gas.plant.dao.UserDao;
import com.security.gas.plant.dto.DistributorAndUsersInputDto;
import com.security.gas.plant.dto.DistributorRequestDto;
import com.security.gas.plant.dto.DistributorResponseDto;
import com.security.gas.plant.dto.UserInputDto;
import com.security.gas.plant.entity.RoleEntity;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.repository.DistributorRepository;
import com.security.gas.plant.repository.RoleRepository;
import com.security.gas.plant.requests.registrationrequest.UserRegisterRequest;
import com.security.gas.plant.service.DistributorService;
import com.security.gas.plant.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributorFacade {

    private final DistributorService distributorService;
    private final UserService userService;
    private final DistributorRepository distributorRepository;
    private final UserDao userDao;
    private final RoleRepository roleRepository;

    @Transactional
    public void createDistributorAndUsers(DistributorAndUsersInputDto distributorAndUsersInputDto) throws MainServiceBusinessException {
        log.info("Starting creation of Distributor and Users: {}", distributorAndUsersInputDto);
        String distributorName = distributorAndUsersInputDto.getName();
        log.info("Checking if distributor with name [{}] already exists", distributorName);
        if (distributorRepository.existsDistributorByName(distributorName)) {
            log.error("Distributor with name [{}] already exists", distributorName);
            throw new DuplicateResourceException("Distributor with name [" + distributorName + "] already exists");
        }
        log.info("Initializing set for user emails");
        Set<String> userEmails = new HashSet<>();
        for (UserInputDto userInputDto : distributorAndUsersInputDto.getUsers()) {
            String userEmail = userInputDto.getEmail();
            log.info("Processing user with email: {}", userEmail);
            if (userDao.existsPersonWithEmail(userEmail)) {
                log.error("User with email [{}] already exists", userEmail);
                throw new DuplicateResourceException("User with email [" + userEmail + "] already exists");
            }
            if (!userEmails.add(userEmail)) {
                log.error("Duplicate email [{}] in the request", userEmail);
                throw new IllegalArgumentException("Duplicate email [" + userEmail + "] in the request");
            }
        }
        DistributorRequestDto distributorRequestDto = new DistributorRequestDto();
        log.info("Proceeding to create distributor: {}", distributorRequestDto);
        distributorRequestDto.setName(distributorName);
        distributorRequestDto.setReg(distributorAndUsersInputDto.getReg());
        distributorRequestDto.setContact(distributorAndUsersInputDto.getContact());
        distributorRequestDto.setAddress(distributorAndUsersInputDto.getAddress());
        distributorRequestDto.setRegionId(distributorAndUsersInputDto.getRegionId());
        log.info("Saving Distributor: {}", distributorRequestDto);
        DistributorResponseDto createdDistributor = distributorService.addNewDistributor(distributorRequestDto);
        log.info("Created Distributor: {}", createdDistributor);

        for (UserInputDto userInputDto : distributorAndUsersInputDto.getUsers()) {
            log.info("Creating User: {}", userInputDto);
            UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
            RoleEntity roleEntity = roleRepository.findByName(userInputDto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role with name [" + userInputDto.getRole() + "] does not exist"));
            userRegisterRequest.setFirstname(userInputDto.getFirstname());
            userRegisterRequest.setLastname(userInputDto.getLastname());
            userRegisterRequest.setEmail(userInputDto.getEmail());
            userRegisterRequest.setPassword(userInputDto.getPassword());
            userRegisterRequest.setRole(roleEntity.getName());
            userRegisterRequest.setDistributorId(createdDistributor.getId());
            userService.register(userRegisterRequest);
            log.info("User [{}] registered successfully", userInputDto.getEmail());
        }
        log.info("Finished creating distributor and associated users");;
    }
}