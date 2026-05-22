package com.auth.application.usecase;

import com.auth.application.command.RegisterCommand;
import com.auth.application.usecase.LoginUseCase.PasswordEncoder;
import com.auth.domain.exception.EmailAlreadyExistsException;
import com.auth.domain.model.Email;
import com.auth.domain.model.Password;
import com.auth.domain.model.Role;
import com.auth.domain.model.User;
import com.auth.domain.repository.UserRepository;

import java.util.UUID;

/**
 * Caso de uso — orquesta el flujo completo del registro.
 *
 * Reglas de negocio que aplica:
 *  1. El email no puede estar ya registrado
 *  2. La contraseña se hashea antes de guardar (nunca en texto plano)
 *  3. Todo usuario nuevo recibe ROLE_USER por defecto
 *  4. Devuelve el id del usuario creado
 *
 * SRP — esta clase solo hace una cosa: manejar el registro.
 * DIP — depende de interfaces, no de implementaciones concretas.
 */
public class RegisterUseCase {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleProvider    roleProvider;

    public RegisterUseCase(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleProvider roleProvider) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleProvider    = roleProvider;
    }

    /**
     * Ejecuta el registro con los datos del RegisterCommand.
     * Devuelve el id del usuario creado.
     */
    public RegisterResult execute(RegisterCommand command) {

        // 1. Verificar que el email no exista
        Email email = new Email(command.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(command.email());
        }

        // 2. Hashear la contraseña — nunca se guarda en texto plano
        String hashedPassword = passwordEncoder.encode(command.password());

        // 3. Crear el usuario nuevo con los Value Objects del dominio
        User user = new User(
                email,
                Password.fromHash(hashedPassword)
        );

        // 4. Asignar ROLE_USER por defecto
        Role userRole = roleProvider.findUserRole();
        user.assignRole(userRole);

        // 5. Guardar en la base de datos
        User savedUser = userRepository.save(user);

        // 6. Devolver el resultado
        return new RegisterResult(savedUser.getId().toString(), savedUser.getEmail().getValue());
    }

    // ─── Resultado del registro ───────────────────────────
    public record RegisterResult(
            String userId,
            String email
    ) {}

    // ─── Puerto para obtener roles ────────────────────────
    // Infrastructure inyecta la implementación que consulta la BD
    public interface RoleProvider {
        Role findUserRole();
        Role findAdminRole();
    }
}