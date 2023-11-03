package com.security.gas.plant.repository;

import com.security.gas.plant.entity.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistributorRepository extends JpaRepository<Distributor, Long> {

    boolean existsDistributorByName(String name);

    boolean existsDistributorById(Long id);

    Optional<Distributor> findDistributorByName(String name);

    List<Distributor> findAllByRegionId(Long regionId);
}