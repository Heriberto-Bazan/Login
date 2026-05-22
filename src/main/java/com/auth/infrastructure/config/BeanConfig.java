package com.auth.infrastructure.config;

import com.auth.application.port.EventPublisher;
import com.auth.application.port.TokenService;
import com.auth.application.usecase.LoginUseCase;
import com.auth.application.usecase.LoginUseCase.PasswordEncoder;
import com.auth.application.usecase.RegisterUseCase;
import com.auth.application.usecase.RegisterUseCase.RoleProvider;
import com.auth.domain.model.Role;
import com.auth.domain.repository.UserRepository;
import com.auth.infrastructure.adapter.persistence.SpringDataRoleRepository;
import com.auth.infrastructure.adapter.security.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeanConfig {

    // ─── JWT Provider (TokenService) ──────────────────────
    @Bean
    public JwtProvider jwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessExp,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshExp) {
        return new JwtProvider(secret, accessExp, refreshExp);
    }

    // ─── Password Encoder ─────────────────────────────────
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder(BCryptPasswordEncoder bcrypt) {
        return new PasswordEncoder() {
            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return bcrypt.matches(rawPassword, encodedPassword);
            }
            @Override
            public String encode(String rawPassword) {
                return bcrypt.encode(rawPassword);
            }
        };
    }

    // ─── RoleProvider ─────────────────────────────────────
    @Bean
    public RoleProvider roleProvider(SpringDataRoleRepository roleRepo) {
        return new RoleProvider() {
            @Override
            public Role findUserRole() {
                return roleRepo.findByName("ROLE_USER")
                        .map(e -> new Role(e.getId(), e.getName()))
                        .orElseThrow(() -> new IllegalStateException("ROLE_USER no encontrado"));
            }
            @Override
            public Role findAdminRole() {
                return roleRepo.findByName("ROLE_ADMIN")
                        .map(e -> new Role(e.getId(), e.getName()))
                        .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN no encontrado"));
            }
        };
    }

    // ─── LoginUseCase ─────────────────────────────────────
    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository,
                                     TokenService tokenService,
                                     EventPublisher eventPublisher,
                                     PasswordEncoder passwordEncoder) {
        return new LoginUseCase(userRepository, tokenService, eventPublisher, passwordEncoder);
    }

    // ─── RegisterUseCase ──────────────────────────────────
    @Bean
    public RegisterUseCase registerUseCase(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder,
                                           RoleProvider roleProvider) {
        return new RegisterUseCase(userRepository, passwordEncoder, roleProvider);
    }
}