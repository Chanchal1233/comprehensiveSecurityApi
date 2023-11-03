package com.security.gas.plant.service;

import com.security.gas.plant.dto.UserDataRequestDto;
import com.security.gas.plant.dto.UserDataResponseDto;
import com.security.gas.plant.dtomapper.UserDataDtoMapper;
import com.security.gas.plant.entity.UserData;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.UserDataRepository;
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
public class UserDataService {

    private final UserDataRepository userDataRepository;

    @Cacheable(value = "userDatas", unless = "#result == null")
    public List<UserDataResponseDto> getAllUserData() throws MainServiceBusinessException {
        List<UserDataResponseDto> userDataResponseDtos;
        try {
            log.info("UserDataService:getAllUserData execution started.");
            List<UserData> userList = userDataRepository.findAll();
            if (!userList.isEmpty()) {
                userDataResponseDtos = userList.stream()
                        .map(UserDataDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                userDataResponseDtos = Collections.emptyList();
            }
            log.debug("UserDataService:getAllUserData retrieving all user data from the database  {}", UserDataDtoMapper.jsonAsString(userDataResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all users from the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all users from the Database", ex);
        }
        log.info("UserDataService:getAllUserData execution ended.");
        return userDataResponseDtos;
    }

    @Cacheable(value = "userDataById", key = "#id", unless = "#result == null")
    public UserDataResponseDto getUserDataById(long id) {
        UserDataResponseDto userDataResponseDto;
        try {
            log.info("UserDataService:getUserDataById execution started.");
            UserData userData = userDataRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User data with id " + id + " not found"));
            userDataResponseDto = UserDataDtoMapper.convertToDto(userData);
            log.debug("UserDataService:getUserDataById retrieving user data from the database with id {} {}", id, UserDataDtoMapper.jsonAsString(userDataResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving user data with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching user data from Database " + id, ex);
        }
        log.info("UserDataService:getUserDataById execution ended.");
        return userDataResponseDto;
    }

    @Caching(evict = {
            @CacheEvict(value = "userDatas", allEntries = true),
            @CacheEvict(value = "userDataById", key = "#id")
    })
    public UserDataResponseDto updateUserDataById(Long id, UserDataRequestDto userDataRequestDto) throws MainServiceBusinessException {
        UserDataResponseDto userDataResponseDto;
        try {
            Optional<UserData> existingUserDataOptional = userDataRepository.findById(id);
            if (existingUserDataOptional.isEmpty()) {
                throw new ResourceNotFoundException("User data with ID " + id + " not found");
            }
            UserData existingUserData = existingUserDataOptional.get();

            log.info("UserDataService:updateUserDataById execution started for ID {}", id);
            UserDataDtoMapper.updateEntity(existingUserData, userDataRequestDto);
            UserData updatedUserData = userDataRepository.save(existingUserData);
            userDataResponseDto = UserDataDtoMapper.convertToDto(updatedUserData);
            log.debug("UserDataService:updateUserDataById received response from Database {}", UserDataDtoMapper.jsonAsString(userDataResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("User data not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating user data entry in the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while updating user data entry", ex);
        }
        log.info("UserDataService:updateUserDataById execution ended for ID {}", id);
        return userDataResponseDto;
    }

    public void deleteUserDataById(Long id) {
        try {
            if (!userDataRepository.existsById(id)) {
                throw new ResourceNotFoundException("User data with id [%s] not found".formatted(id));
            }
            log.debug("UserDataService:deleteUserDataById execution started");
            userDataRepository.deleteById(id);
            log.debug("UserDataService:deleteUserDataById deleted user data from the database with id {}", id);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting user data with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while deleting user data from the database " + id, ex);
        }
        log.info("UserDataService:deleteUserDataById execution ended");
    }
}