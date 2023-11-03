package com.security.gas.plant.jpadataaccessservice;

import com.security.gas.plant.dao.UserDao;
import com.security.gas.plant.entity.User;
import com.security.gas.plant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserJpaDataAccessRepository implements UserDao {

    private final UserRepository userRepository;

    @Override
    public List<User> selectAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> selectCompUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    @Override
    public void deleteCompUserById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return userRepository.existsUserById(id);
    }

    @Override
    public void updateCompUser(User update) {
        userRepository.save(update);
    }

    @Override
    public List<User> selectUsersByCompanyId(Long organizationId) {
        return userRepository.findByCompanyId(organizationId);
    }

    @Override
    public List<User> selectUsersByDistributorId(Long distributorId) {
        return userRepository.findByDistributorId(distributorId);
    }

    @Override
    public List<User> selectUsersWithCompany() {
        return userRepository.findByCompanyIsNotNull();
    }

    @Override
    public List<User> selectUsersWithDistributor() {
        return userRepository.findByDistributorIsNotNull();
    }
}