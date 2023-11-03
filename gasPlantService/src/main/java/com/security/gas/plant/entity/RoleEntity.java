package com.security.gas.plant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<PermissionEntity> permissions = new HashSet<>();


    @OneToMany(mappedBy = "role")
    private Set<User> users = new HashSet<>();

    public RoleEntity(Long id, String roleName, Set<PermissionEntity> permissions) {
        this.id = id;
        this.name = roleName;
        this.permissions = permissions;
    }

    public Set<GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(permission -> (GrantedAuthority) permission)
                .collect(Collectors.toSet());
    }
}