package com.security.gas.plant.dao;


import com.security.gas.plant.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {


    List<User> selectAllUsers();

    Optional<User> selectCompUserById(Integer id);

    boolean existsPersonWithEmail(String email);

    void deleteCompUserById(Integer id);

    boolean existsPersonWithId(Integer id);

    void updateCompUser(User update);

    List<User> selectUsersByCompanyId(Long organizationId);

    List<User> selectUsersByDistributorId(Long distributorId);

    List<User> selectUsersWithCompany();

    List<User> selectUsersWithDistributor();
}