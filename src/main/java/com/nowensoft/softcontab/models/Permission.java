package com.nowensoft.softcontab.models;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.*;

@Entity
@Table(name = "permissions")
public class Permission  implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getAuthority() {
       return nombre;
    }

    @Override
    public String toString() {
        return "Permission [id=" + id + ", nombre=" + nombre + "]";
    }

    

}
