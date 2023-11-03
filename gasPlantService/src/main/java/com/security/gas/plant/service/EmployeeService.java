package com.security.gas.plant.service;

import com.security.gas.plant.dto.EmployeeRequestDto;
import com.security.gas.plant.dto.EmployeeResponseDto;
import com.security.gas.plant.dtomapper.EmployeeDtoMapper;
import com.security.gas.plant.entity.Employee;
import com.security.gas.plant.exception.MainServiceBusinessException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.DistributorRepository;
import com.security.gas.plant.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DistributorRepository distributorRepository;

    @Cacheable(value = "employees", unless = "#result == null")
    public List<EmployeeResponseDto> getAllEmployees() throws MainServiceBusinessException {
        List<EmployeeResponseDto> employeeResponseDtos;
        try {
            log.info("EmployeeService:getAllEmployees execution started.");
            List<Employee> employeeList = employeeRepository.findAll();
            if (!employeeList.isEmpty()) {
                employeeResponseDtos = employeeList.stream()
                        .map(EmployeeDtoMapper::convertToDto)
                        .collect(Collectors.toList());
            } else {
                employeeResponseDtos = Collections.emptyList();
            }
            log.debug("EmployeeService:getAllEmployees retrieving all employee data from the database  {}", EmployeeDtoMapper.jsonAsString(employeeResponseDtos));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving all employees from the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching all employees from the Database", ex);
        }
        log.info("EmployeeService:getAllEmployees execution ended.");
        return employeeResponseDtos;
    }

    @Cacheable(value = "employeeById", key = "#id", unless = "#result == null")
    public EmployeeResponseDto getEmployeeById(long id) {
        EmployeeResponseDto employeeResponseDto;
        try {
            log.info("EmployeeService:getEmployeeById execution started.");
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + id + " not found"));
            employeeResponseDto = EmployeeDtoMapper.convertToDto(employee);
            log.debug("EmployeeService:getEmployeeById retrieving employee from the database with id {} {}", id, EmployeeDtoMapper.jsonAsString(employeeResponseDto));
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving employee with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching employee from Database " + id, ex);
        }
        log.info("EmployeeService:getEmployeeById execution ended.");
        return employeeResponseDto;
    }

    @Cacheable(value = "employeeByName", key = "#name", unless = "#result == null")
    public EmployeeResponseDto getEmployeeByName(String name) {
        EmployeeResponseDto employeeResponseDto;
        try {
            log.info("EmployeeService:getEmployeeByName execution started.");
            Employee employee = employeeRepository.findEmployeeByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee with name " + name + " not found"));
            employeeResponseDto = EmployeeDtoMapper.convertToDto(employee);
            log.debug("EmployeeService:getEmployeeByName retrieving employee from the database with name {} {}", name, EmployeeDtoMapper.jsonAsString(employeeResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Employee not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while retrieving employee with name {} from the database, Exception message {}", name, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while fetching employee from Database " + name, ex);
        }
        log.info("EmployeeService:getEmployeeByName execution ended.");
        return employeeResponseDto;
    }

    @Caching(evict = {
            @CacheEvict(value = "employees", allEntries = true),
            @CacheEvict(value = "employeeById", key = "#id"),
            @CacheEvict(value = "employeeByName", key = "#employeeRequestDto.name"),
    })
    public EmployeeResponseDto updateEmployeeById(Long id, EmployeeRequestDto employeeRequestDto) throws MainServiceBusinessException {
        EmployeeResponseDto employeeResponseDto;
        try {
            Optional<Employee> existingEmployeeOptional = employeeRepository.findById(id);
            if (existingEmployeeOptional.isEmpty()) {
                throw new ResourceNotFoundException("Employee with ID " + id + " not found");
            }
            Employee existingEmployee = existingEmployeeOptional.get();
            log.info("EmployeeService:updateEmployeeById execution started for ID {}", id);
            EmployeeDtoMapper.updateEntity(existingEmployee, employeeRequestDto, distributorRepository);
            Employee updatedEmployee = employeeRepository.save(existingEmployee);
            employeeResponseDto = EmployeeDtoMapper.convertToDto(updatedEmployee);
            log.debug("EmployeeService:updateEmployeeById received response from Database {}", EmployeeDtoMapper.jsonAsString(employeeResponseDto));
        } catch (ResourceNotFoundException ex) {
            log.error("Employee not found: " + ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception occurred while updating employee entry in the database, Exception message {}", ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while updating employee data entry", ex);
        }
        log.info("EmployeeService:updateEmployeeById execution ended for ID {}", id);
        return employeeResponseDto;
    }

    public void deleteEmployeeById(Long id) {
        try {
            if (!employeeRepository.existsById(id)) {
                throw new ResourceNotFoundException("Employee with id " + id + " not found");
            }
            log.debug("EmployeeService:deleteEmployeeById execution started");
            employeeRepository.deleteById(id);
            log.debug("EmployeeService:deleteEmployeeById deleted employee data from the database with id {}", id);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting employee with id {} from the database, Exception message {}", id, ex.getMessage());
            throw new MainServiceBusinessException("Exception occurred while deleting employee from the database " + id, ex);
        }
        log.info("EmployeeService:deleteEmployeeById execution ended");
    }
}