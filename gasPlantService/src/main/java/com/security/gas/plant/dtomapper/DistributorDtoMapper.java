package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.DistributorRequestDto;
import com.security.gas.plant.dto.DistributorResponseDto;
import com.security.gas.plant.entity.Distributor;
import com.security.gas.plant.entity.Region;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.RegionRepository;

public class DistributorDtoMapper {

    public static Distributor convertToEntity(DistributorRequestDto distributorRequestDto, RegionRepository regionRepository) {
        Distributor distributor = new Distributor();
        distributor.setName(distributorRequestDto.getName());
        distributor.setReg(distributorRequestDto.getReg());
        distributor.setContact(distributorRequestDto.getContact());
        distributor.setAddress(distributorRequestDto.getAddress());
        Long regionId = distributorRequestDto.getRegionId();
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region with ID " + regionId + " not found"));
        distributor.setRegion(region);

        return distributor;
    }

    public static DistributorResponseDto convertToDto(Distributor distributor) {
        DistributorResponseDto distributorResponseDto = new DistributorResponseDto();
        distributorResponseDto.setId(distributor.getId());
        distributorResponseDto.setName(distributor.getName());
        distributorResponseDto.setReg(distributor.getReg());
        distributorResponseDto.setContact(distributor.getContact());
        distributorResponseDto.setAddress(distributor.getAddress());
        if (distributor.getRegion() != null) {
            distributorResponseDto.setRegionId(distributor.getRegion().getId());
        }
        return distributorResponseDto;

    }

    public static void updateEntity(Distributor existingDistributor, DistributorRequestDto distributorRequestDto, RegionRepository regionRepository) {
        existingDistributor.setName(distributorRequestDto.getName());
        existingDistributor.setReg(distributorRequestDto.getReg());
        existingDistributor.setContact(distributorRequestDto.getContact());
        existingDistributor.setAddress(distributorRequestDto.getAddress());
        Long regionId = distributorRequestDto.getRegionId();
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new ResourceNotFoundException("Region with ID " + regionId + " not found"));
        existingDistributor.setRegion(region);
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