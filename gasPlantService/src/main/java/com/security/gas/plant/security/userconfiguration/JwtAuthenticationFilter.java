package com.security.gas.plant.security.userconfiguration;

import com.security.gas.plant.entity.PermissionEntity;
import com.security.gas.plant.entity.User;
import com.security.gas.plant.repository.PermissionRepository;
import com.security.gas.plant.security.usertoken.TokenRepository;
import com.security.gas.plant.service.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private final PermissionRepository permissionRepository;
    private final RedisTokenService redisTokenService;
    private final RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if (servletPath.contains("/api/v1/auth") ||
                servletPath.startsWith("/assets/") ||
                servletPath.startsWith("/actuator/") ||
                servletPath.startsWith("/instances/") ||
                servletPath.startsWith("/applications/")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(7);
        if (redisTokenService.isTokenBlacklisted(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!jwtService.isValidSignature(jwt)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Map<Object, Object> tokenData = redisTokenService.getTokenDataFromRedis(jwt);
        if (tokenData != null) {
            String userEmail = (String) tokenData.get("username");
            String roles = (String) tokenData.get("roles");
            String permissions = (String) tokenData.get("permissions");

            UserDetails userDetails = constructUserDetailsFromRedisData(userEmail, roles, permissions);
            UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails, roles, permissions);
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
            return;
        }
        final String userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                List<GrantedAuthority> authorities = extractAuthoritiesFromJWT(jwt);
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                redisTokenService.saveUserToRedis(userDetails);
            }
        }
        filterChain.doFilter(request, response);
    }

    private List<GrantedAuthority> extractAuthoritiesFromJWT(String jwt) {
        List<String> roles = jwtService.extractRoles(jwt);
        List<GrantedAuthority> authorities = new ArrayList<>(roles.stream().map(SimpleGrantedAuthority::new).toList());
        if (roles.contains("ROLE_SUPER-ADMIN")) {
            List<String> allPermissions = permissionRepository.findAll()
                    .stream()
                    .map(PermissionEntity::getName)
                    .toList();
            authorities.addAll(allPermissions.stream().map(SimpleGrantedAuthority::new).toList());
        } else {
            List<String> permissions = jwtService.extractPermissions(jwt);
            authorities.addAll(permissions.stream().map(SimpleGrantedAuthority::new).toList());
        }
        return authorities;
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails userDetails, String roles, String permissions) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            authorities.addAll(Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        if (roles != null && roles.contains("ROLE_SUPER-ADMIN")) {
            List<String> allPermissions = permissionRepository.findAll().stream().map(PermissionEntity::getName).toList();
            authorities.addAll(allPermissions.stream().map(SimpleGrantedAuthority::new).toList());
        } else if (permissions != null && !permissions.isEmpty()) {
            authorities.addAll(Arrays.stream(permissions.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    private UserDetails constructUserDetailsFromRedisData(String email, String roles, String permissions) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            grantedAuthorities.addAll(Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        if (permissions != null && !permissions.isEmpty()) {
            grantedAuthorities.addAll(Arrays.stream(permissions.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList());
        }
        String firstname = (String) redisTemplate.opsForHash().get("user::" + email, "firstname");
        String lastname = (String) redisTemplate.opsForHash().get("user::" + email, "lastname");
        String password = (String) redisTemplate.opsForHash().get("user::" + email, "password");
        User user = new User();
        user.setEmail(email);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPassword(password);
        return user;
    }
}