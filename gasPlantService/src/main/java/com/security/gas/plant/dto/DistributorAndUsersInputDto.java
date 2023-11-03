package com.security.gas.plant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributorAndUsersInputDto {
    private String name;
    private Integer reg;
    private Long contact;
    private String address;
    private Long regionId;
    private List<UserInputDto> users;
}
