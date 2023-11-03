package com.security.gas.plant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String association;
    private Object associationDetails;
    private Object regionDetails;
}