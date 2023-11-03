package com.security.gas.plant.dto;

import lombok.Data;

@Data
public class UserResponseDto {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String role;
    private Long companyId;
}
