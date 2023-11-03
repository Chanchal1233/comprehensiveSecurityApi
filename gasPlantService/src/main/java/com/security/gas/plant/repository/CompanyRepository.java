package com.security.gas.plant.repository;

import com.security.gas.plant.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsCompanyByName(String name);

    Optional<Company> findCompanyByName(String name);

    List<Company> findAllByOrganizationId(Long organizationId);

    boolean existsCompanyById(Long id);

    default boolean existsAny() {
        return count() > 0;
    }
}