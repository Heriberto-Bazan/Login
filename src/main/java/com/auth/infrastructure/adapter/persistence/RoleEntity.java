package com.auth.infrastructure.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entidad JPA — representa la fila en la tabla 'roles' de PostgreSQL.
 */
@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    protected RoleEntity() {}

    public RoleEntity(UUID id, String name) {
        this.id   = id;
        this.name = name;
    }

    public UUID   getId()   { return id; }
    public String getName() { return name; }

    public void setId(UUID id)       { this.id = id; }
    public void setName(String name) { this.name = name; }
}