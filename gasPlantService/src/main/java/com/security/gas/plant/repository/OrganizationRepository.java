package com.security.gas.plant.repository;

import com.security.gas.plant.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsOrganizationByName(String name);

    boolean existsOrganizationById(Long id);

    Optional<Organization> findOrganizationByName(String name);

    default boolean existsAny() {
        return count() > 0;
    }
}