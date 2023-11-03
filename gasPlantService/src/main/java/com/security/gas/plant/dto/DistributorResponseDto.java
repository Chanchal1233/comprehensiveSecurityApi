package com.security.gas.plant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DistributorResponseDto implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Integer reg;
    private Long contact;
    private String address;
    private Long regionId;
}