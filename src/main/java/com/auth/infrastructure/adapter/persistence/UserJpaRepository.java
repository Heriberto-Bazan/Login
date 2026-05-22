package com.auth.infrastructure.adapter.persistence;

import com.auth.domain.model.Email;
import com.auth.domain.model.Password;
import com.auth.domain.model.Role;
import com.auth.domain.model.User;
import com.auth.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia — implementa el puerto UserRepository del Domain.
 *
 * Responsabilidades:
 *  1. Delega las queries a SpringDataUserRepository (Spring Data JPA)
 *  2. Traduce UserEntity → User del dominio (y viceversa)
 *
 * El Domain nunca ve JPA ni UserEntity — solo trabaja con User.
 * Este archivo es el único que conoce los dos mundos.
 */
@Repository
public class UserJpaRepository implements UserRepository {

    // Spring Data JPA — genera las queries automáticamente
    private final SpringDataUserRepository springDataRepo;

    public UserJpaRepository(SpringDataUserRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved  = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springDataRepo
                .findByEmail(email.getValue())
                .map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springDataRepo
                .findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springDataRepo.existsByEmail(email.getValue());
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepo.deleteById(id);
    }

    // ─── Traducción Entity → Domain ───────────────────────
    private User toDomain(UserEntity entity) {
        Set<Role> roles = entity.getRoles().stream()
                .map(r -> new Role(r.getId(), r.getName()))
                .collect(Collectors.toSet());

        return new User(
                entity.getId(),
                new Email(entity.getEmail()),
                Password.fromHash(entity.getPassword()),
                entity.isEnabled(),
                roles,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    // ─── Traducción Domain → Entity ───────────────────────
    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity(
                user.getId(),
                user.getEmail().getValue(),
                user.getPassword().getValue(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        Set<RoleEntity> roleEntities = user.getRoles().stream()
                .map(r -> new RoleEntity(r.getId(), r.getName()))
                .collect(Collectors.toSet());

        entity.setRoles(roleEntities);
        return entity;
    }
}