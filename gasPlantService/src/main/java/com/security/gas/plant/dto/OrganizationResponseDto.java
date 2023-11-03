package com.security.gas.plant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationResponseDto {
    private Long id;
    private String name;
    private Integer reg;
    private String industry;
    private String location;
    private Integer contact;
}