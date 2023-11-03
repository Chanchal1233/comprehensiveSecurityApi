package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.UserDataRequestDto;
import com.security.gas.plant.dto.UserDataResponseDto;
import com.security.gas.plant.entity.UserData;

public class UserDataDtoMapper {

    public static UserData convertToEntity(UserDataRequestDto userDataRequestDto) {
        UserData userData = new UserData();
        userData.setAddress(userDataRequestDto.getAddress());
        userData.setMobileNo(userDataRequestDto.getMobileNo());
        return userData;
    }

    public static UserDataResponseDto convertToDto(UserData userData) {
        UserDataResponseDto userDataResponseDto = new UserDataResponseDto();
        userDataResponseDto.setId(userData.getId());
        userDataResponseDto.setAddress(userData.getAddress());
        userDataResponseDto.setMobileNo(userData.getMobileNo());
        return userDataResponseDto;
    }

    public static void updateEntity(UserData existingUserData, UserDataRequestDto userDataRequestDto) {
        existingUserData.setAddress(userDataRequestDto.getAddress());
        existingUserData.setMobileNo(userDataRequestDto.getMobileNo());
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