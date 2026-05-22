package com.auth.domain.repository;

import com.auth.domain.model.Email;
import com.auth.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto del dominio — define QUÉ operaciones necesita el negocio.
 *
 * Esta interface vive en el Domain pero la IMPLEMENTA Infrastructure
 * (UserJpaRepository.java). Así el dominio nunca sabe que existe JPA.
 *
 * Principio DIP aplicado: LoginUseCase depende de esta interface,
 * no de la implementación concreta con JPA.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(Email email);

    Optional<User> findById(UUID id);

    boolean existsByEmail(Email email);

    void deleteById(UUID id);
}