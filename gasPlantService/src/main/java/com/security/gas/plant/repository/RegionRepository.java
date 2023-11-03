package com.security.gas.plant.repository;

import com.security.gas.plant.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    boolean existsRegionByName(String name);

    boolean existsRegionById(Long id);

    Optional<Region> findRegionByName(String name);

    List<Region> findAllByCompanyId(Long organizationId);

    default boolean existsAny() {
        return count() > 0;
    }
}