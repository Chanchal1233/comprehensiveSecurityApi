package com.security.gas.plant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String field;
    private String errorMessage;
}