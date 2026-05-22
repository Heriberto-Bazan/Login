package com.auth.infrastructure.adapter.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entidad JPA — representa la fila en la tabla 'users' de PostgreSQL.
 *
 * IMPORTANTE: esta clase NO es el User del dominio.
 * Es solo un mapeo técnico de la tabla — vive en Infrastructure.
 *
 * UserJpaRepository traduce entre UserEntity ↔ User del dominio.
 */
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relación muchos a muchos con roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    // ─── Constructor vacío requerido por JPA ──────────────
    protected UserEntity() {}

    // ─── Constructor completo ─────────────────────────────
    public UserEntity(UUID id, String email, String password,
                      boolean enabled, LocalDateTime createdAt,
                      LocalDateTime updatedAt) {
        this.id        = id;
        this.email     = email;
        this.password  = password;
        this.enabled   = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ─── Getters y Setters ────────────────────────────────
    public UUID          getId()        { return id; }
    public String        getEmail()     { return email; }
    public String        getPassword()  { return password; }
    public boolean       isEnabled()    { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<RoleEntity> getRoles()   { return roles; }

    public void setId(UUID id)                   { this.id = id; }
    public void setEmail(String email)           { this.email = email; }
    public void setPassword(String password)     { this.password = password; }
    public void setEnabled(boolean enabled)      { this.enabled = enabled; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setRoles(Set<RoleEntity> roles)  { this.roles = roles; }
}