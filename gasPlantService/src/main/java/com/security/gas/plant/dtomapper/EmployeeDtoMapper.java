package com.security.gas.plant.dtomapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dto.EmployeeRequestDto;
import com.security.gas.plant.dto.EmployeeResponseDto;
import com.security.gas.plant.entity.Distributor;
import com.security.gas.plant.entity.Employee;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.DistributorRepository;

public class EmployeeDtoMapper {

    public static Employee convertToEntity(EmployeeRequestDto employeeRequestDto, DistributorRepository distributorRepository) {
        Employee employee = new Employee();
        employee.setName(employeeRequestDto.getName());
        employee.setType(employeeRequestDto.getType());

        Long distributorId = employeeRequestDto.getDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ResourceNotFoundException("Distributor with ID " + distributorId + " not found"));
        employee.setDistributor(distributor);

        return employee;
    }

    public static EmployeeResponseDto convertToDto(Employee employee) {
        EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto();
        employeeResponseDto.setId(employee.getId());
        employeeResponseDto.setName(employee.getName());
        employeeResponseDto.setType(employee.getType());

        if (employee.getDistributor() != null) {
            employeeResponseDto.setDistributorId(employee.getDistributor().getId());
        }

        return employeeResponseDto;
    }

    public static void updateEntity(Employee existingEmployee, EmployeeRequestDto employeeRequestDto, DistributorRepository distributorRepository) {
        existingEmployee.setName(employeeRequestDto.getName());
        existingEmployee.setType(employeeRequestDto.getType());

        Long distributorId = employeeRequestDto.getDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ResourceNotFoundException("Distributor with ID " + distributorId + " not found"));
        existingEmployee.setDistributor(distributor);
    }

    public static String jsonAsString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}