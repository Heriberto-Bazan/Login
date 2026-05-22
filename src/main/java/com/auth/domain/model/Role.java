package com.auth.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad del dominio — representa un rol del sistema.
 *
 * Valores posibles: ROLE_USER, ROLE_ADMIN
 *
 * Sin @Entity ni @Table — eso vive en Infrastructure (RoleEntity.java).
 * Esta clase es el rol tal como lo entiende el negocio.
 */
public class Role {

    private final UUID id;
    private final String name;

    public Role(UUID id, String name) {
        Objects.requireNonNull(id,   "El id del rol no puede ser nulo");
        Objects.requireNonNull(name, "El nombre del rol no puede ser nulo");

        if (name.isBlank()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }

        this.id   = id;
        this.name = name;
    }

    // Factory methods — nombres de rol como constantes del negocio
    public static Role userRole(UUID id) {
        return new Role(id, "ROLE_USER");
    }

    public static Role adminRole(UUID id) {
        return new Role(id, "ROLE_ADMIN");
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Role{name=" + name + "}";
    }
}