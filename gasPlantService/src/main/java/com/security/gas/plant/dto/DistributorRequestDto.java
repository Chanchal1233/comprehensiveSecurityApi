package com.security.gas.plant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DistributorRequestDto {
    private String name;
    private Integer reg;
    private Long contact;
    private String address;
    private Long regionId;
}