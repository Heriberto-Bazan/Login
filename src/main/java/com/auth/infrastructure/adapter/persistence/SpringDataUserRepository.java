package com.auth.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface de Spring Data JPA — genera las queries automáticamente.
 *
 * Spring genera la implementación en tiempo de ejecución.
 * Solo necesitamos declarar los métodos con el nombre correcto.
 *
 * Esta interface NO es el puerto del Domain — es un detalle técnico
 * de Infrastructure. Por eso vive aquí y no en el Domain.
 */
public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}