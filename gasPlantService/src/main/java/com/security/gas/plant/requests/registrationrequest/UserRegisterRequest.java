package com.security.gas.plant.requests.registrationrequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
    private Long companyId;
    private Long distributorId;
}