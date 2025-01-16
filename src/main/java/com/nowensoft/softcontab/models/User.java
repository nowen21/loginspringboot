package com.nowensoft.softcontab.models;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;



    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_has_permissions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_has_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // Añadir los permisos directos del usuario
        this.permissions.forEach(grantedAuthorities::add);

        // Añadir los roles del usuario (prefijo ROLE_)
        /*
         * this.roles.forEach(rol ->
         * grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
         * );
         */

        // Añadir los permisos heredados de los roles
        this.roles.forEach(role -> grantedAuthorities.addAll(role.getPermissions()));

        return grantedAuthorities; 
    }

    /*
     * @Override
     * public Collection<? extends GrantedAuthority> getAuthorities() {
     * return authorities.stream()
     * .map(authority -> (GrantedAuthority) authority)
     * .collect(Collectors.toSet());
     * }
     */



    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
