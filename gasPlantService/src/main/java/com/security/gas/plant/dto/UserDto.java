package com.security.gas.plant.dto;

import java.util.List;

public record UserDto(

        Integer id,
        String firstname,
        String lastname,
        String email,
        List<String> roles,
        String username
) {
}