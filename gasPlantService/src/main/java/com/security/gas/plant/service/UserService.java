package com.security.gas.plant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.gas.plant.dao.UserDao;
import com.security.gas.plant.dto.UserDto;
import com.security.gas.plant.dtomapper.UserDtoMapper;
import com.security.gas.plant.entity.*;
import com.security.gas.plant.exception.DuplicateResourceException;
import com.security.gas.plant.exception.RequestValidationException;
import com.security.gas.plant.exception.ResourceNotFoundException;
import com.security.gas.plant.repository.*;
import com.security.gas.plant.requests.registrationrequest.UserRegisterRequest;
import com.security.gas.plant.requests.updaterequest.UserUpdateRequest;
import com.security.gas.plant.security.userauthentication.UserAuthenticationRequest;
import com.security.gas.plant.security.userauthentication.UserAuthenticationResponse;
import com.security.gas.plant.security.userconfiguration.JwtService;
import com.security.gas.plant.security.usertoken.Token;
import com.security.gas.plant.security.usertoken.TokenRepository;
import com.security.gas.plant.security.usertoken.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDao userDao;
    private final UserDtoMapper userDtoMapper;
    private final DistributorRepository distributorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final CompanyRepository companyRepository;

    public UserAuthenticationResponse register(UserRegisterRequest request) {
        try {
            String email = request.getEmail();
            if (userDao.existsPersonWithEmail(email))
                throw new DuplicateResourceException("Organization user with email [%s] already exists".formatted(email));
            Long distributorId = request.getDistributorId();
            Long companyId = request.getCompanyId();
            if (distributorId != null && companyId != null) {
                throw new IllegalArgumentException("A user can either belong to a distributor or an organization, not both.");
            }
            Distributor distributor = null;
            Company company = null;
            if (distributorId != null) {
                distributor = distributorRepository.findById(distributorId)
                        .orElseThrow(() -> new IllegalArgumentException("Distributor with id [" + distributorId + "] does not exist"));
            }
            if (companyId != null) {
                company = companyRepository.findById(companyId)
                        .orElseThrow(() -> new IllegalArgumentException("Organization with id [" + companyId + "] does not exist"));
            }
            RoleEntity role = roleRepository.findByNameWithPermissions(request.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role with name [" + request.getRole() + "] does not exist"));
            var user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .distributor(distributor)
                    .company(company)
                    .build();
            UserData userData = new UserData();
            userData.setUser(user);
            user.setUserData(userData);
            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);
            return UserAuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception ex) {
            log.error("Exception occurred during user registration: {}", ex.getMessage());
            throw ex;
        }
    }

    public UserAuthenticationResponse authenticate(UserAuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = repository.findUserByEmail(request.getEmail())
                    .orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return UserAuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception ex) {
            log.error("Exception occurred during user authentication: {}", ex.getMessage());
            throw ex;
        }
    }

    public List<UserDto> getAllUsers() {
        try {
            return userDao.selectAllUsers()
                    .stream()
                    .map(userDtoMapper)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception occurred while fetching all users: {}", ex.getMessage());
            throw ex;
        }
    }

    public UserDto getUser(Integer id) {
        try {
            return userDao.selectCompUserById(id)
                    .map(userDtoMapper)
                    .orElseThrow(() -> new ResourceNotFoundException("Company user with id: [%s] not found".formatted(id)));
        } catch (Exception ex) {
            log.error("Exception occurred while fetching user by ID: {}", ex.getMessage());
            throw ex;
        }
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
    }

    public List<UserDto> getUsersByCompanyId(Long organizationId) {
        try {
            return userDao.selectUsersByCompanyId(organizationId)
                    .stream()
                    .map(userDtoMapper)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users by Organization ID: {}", ex.getMessage());
            throw ex;
        }
    }

    public List<UserDto> getUsersByDistributorId(Long distributorId) {
        try {
            return userDao.selectUsersByDistributorId(distributorId)
                    .stream()
                    .map(userDtoMapper)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users by Distributor ID: {}", ex.getMessage());
            throw ex;
        }
    }

    public List<UserDto> getUsersWithCompany() {
        try {
            return userDao.selectUsersWithCompany()
                    .stream()
                    .map(userDtoMapper)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users with Organization: {}", ex.getMessage());
            throw ex;
        }
    }

    public List<UserDto> getUsersWithDistributor() {
        try {
            return userDao.selectUsersWithDistributor()
                    .stream()
                    .map(userDtoMapper)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Exception occurred while fetching users with Distributor: {}", ex.getMessage());
            throw ex;
        }
    }

    public void deleteUserById(Integer id) {
        try {
            if (!userDao.existsPersonWithId(id))
                throw new ResourceNotFoundException("Organization user with id: [%s] already deleted".formatted(id));
            userDao.deleteCompUserById(id);
        } catch (Exception ex) {
            log.error("Exception occurred while deleting user by ID: {}", ex.getMessage());
            throw ex;
        }
    }

    public void updateUser(Integer id, UserUpdateRequest updateRequest) {
        try {
            User user = userDao.selectCompUserById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Organization user with id: [%s] not found".formatted(id)));
            boolean changes = false;
            if (updateRequest.email() != null && !updateRequest.email().equals(user.getEmail())) {
                if (userDao.existsPersonWithEmail((updateRequest.email()))) {
                    throw new DuplicateResourceException("email already taken");
                }
                user.setEmail(updateRequest.email());
                changes = true;
            }
            if (!changes) {
                throw new RequestValidationException("no changes have been found");
            }
            userDao.updateCompUser(user);
        } catch (Exception ex) {
            log.error("Exception occurred while updating user by ID: {}", ex.getMessage());
            throw ex;
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        try {
            var token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(token);
        } catch (Exception ex) {
            log.error("Exception occurred while saving user token: {}", ex.getMessage());
            throw ex;
        }
    }

    private void revokeAllUserTokens(User user) {
        try {
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
            if (validUserTokens.isEmpty())
                return;
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        } catch (Exception ex) {
            log.error("Exception occurred while revoking user tokens: {}", ex.getMessage());
            throw ex;
        }
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String refreshToken;
            final String userEmail;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return;
            }
            refreshToken = authHeader.substring(7);
            userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail != null) {
                var user = this.repository.findUserByEmail(userEmail)
                        .orElseThrow();
                if (jwtService.isTokenValid(refreshToken, user)) {
                    var accessToken = jwtService.generateToken(user);
                    revokeAllUserTokens(user);
                    saveUserToken(user, accessToken);
                    var authResponse = UserAuthenticationResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                }
            }
        } catch (Exception ex) {
            log.error("Exception occurred during token refresh: {}", ex.getMessage());
            throw ex;
        }
    }
}