package com.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad raíz del dominio (Aggregate Root).
 *
 * Representa un usuario tal como lo entiende el NEGOCIO:
 *  - Tiene email (validado), contraseña (validada), roles y estado.
 *  - Contiene comportamiento real: activate(), disable(), assignRole().
 *  - Sin @Entity, @Table ni ninguna anotación de framework.
 *
 * Esta es la clase más importante del proyecto — todo lo demás gira alrededor de ella.
 */
public class User {

    private final UUID id;
    private final Email email;
    private Password password;
    private boolean enabled;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(Email email, Password password) {
        this.id        = UUID.randomUUID();
        this.email     = Objects.requireNonNull(email,    "El email no puede ser nulo");
        this.password  = Objects.requireNonNull(password, "La contraseña no puede ser nula");
        this.enabled   = true;
        this.roles     = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public User(UUID id, Email email, Password password,
                boolean enabled, Set<Role> roles,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id        = Objects.requireNonNull(id,       "El id no puede ser nulo");
        this.email     = Objects.requireNonNull(email,    "El email no puede ser nulo");
        this.password  = Objects.requireNonNull(password, "La contraseña no puede ser nula");
        this.enabled   = enabled;
        this.roles     = new HashSet<>(roles);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt no puede ser nulo");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt no puede ser nulo");
    }

    // ─── Comportamiento de negocio ─────────────────────────

    /**
     * Activa la cuenta del usuario.
     * Regla: un usuario deshabilitado puede reactivarse.
     */
    public void activate() {
        this.enabled   = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deshabilita la cuenta del usuario.
     * Regla: no se elimina — se deshabilita para mantener el historial.
     */
    public void disable() {
        this.enabled   = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Asigna un rol al usuario.
     * Regla: un usuario no puede tener el mismo rol dos veces.
     */
    public void assignRole(Role role) {
        Objects.requireNonNull(role, "El rol no puede ser nulo");
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cambia la contraseña del usuario.
     * Regla: la nueva contraseña ya viene validada como Value Object.
     */
    public void changePassword(Password newPassword) {
        this.password  = Objects.requireNonNull(newPassword, "La nueva contraseña no puede ser nula");
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verifica si el usuario tiene un rol específico.
     */
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }

    public UUID          getId()        { return id; }
    public Email         getEmail()     { return email; }
    public Password      getPassword()  { return password; }
    public boolean       isEnabled()    { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Copia defensiva — nadie puede modificar el Set desde afuera
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    // ─── Igualdad por identidad (id) ──────────────────────
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email=" + email + ", enabled=" + enabled + "}";
    }
}