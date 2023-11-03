package com.security.gas.plant.repository;

import com.security.gas.plant.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findEmployeeByName(String name);
    boolean existsEmployeeByName(String name);
}