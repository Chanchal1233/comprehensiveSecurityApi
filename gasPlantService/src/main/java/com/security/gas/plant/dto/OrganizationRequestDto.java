package com.security.gas.plant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrganizationRequestDto {
    private String name;
    private Integer reg;
    private String industry;
    private String location;
    private Integer contact;
}
