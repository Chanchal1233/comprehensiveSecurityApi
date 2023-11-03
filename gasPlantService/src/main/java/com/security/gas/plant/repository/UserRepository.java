package com.security.gas.plant.repository;

import com.security.gas.plant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsUserByEmail(String email);

    boolean existsUserById(Integer id);

    Optional<User> findUserByEmail(String email);

    List<User> findByCompanyId(Long organizationId);

    List<User> findByDistributorId(Long distributorId);

    List<User> findByCompanyIsNotNull();

    List<User> findByDistributorIsNotNull();

    default boolean existsAnyUser() {
        return count() > 0;
    }
}