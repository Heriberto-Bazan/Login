package com.auth.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface Spring Data JPA para la tabla roles.
 * Spring genera el SQL automáticamente.
 */
public interface SpringDataRoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);
}