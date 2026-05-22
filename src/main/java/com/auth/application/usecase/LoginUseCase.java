package com.auth.application.usecase;

import com.auth.application.command.LoginCommand;
import com.auth.application.port.EventPublisher;
import com.auth.application.port.TokenService;
import com.auth.domain.event.UserLoggedInEvent;
import com.auth.domain.exception.InvalidCredentialsException;
import com.auth.domain.exception.UserNotFoundException;
import com.auth.domain.model.Email;
import com.auth.domain.model.User;
import com.auth.domain.repository.UserRepository;

/**
 * Caso de uso — orquesta el flujo completo del login.
 *
 * Reglas de negocio que aplica:
 *  1. El usuario debe existir
 *  2. La cuenta debe estar activa (enabled = true)
 *  3. La contraseña debe coincidir con el hash guardado
 *  4. Si todo es correcto, genera los tokens y publica el evento
 *
 * SRP — esta clase solo hace una cosa: manejar el login.
 * DIP — depende de interfaces (UserRepository, TokenService, EventPublisher),
 *        no de implementaciones concretas (JPA, JJWT, Kafka).
 */
public class LoginUseCase {

    private final UserRepository  userRepository;
    private final TokenService    tokenService;
    private final EventPublisher  eventPublisher;
    private final PasswordEncoder passwordEncoder;

    // Inyección por constructor — la forma más limpia y testeable
    public LoginUseCase(UserRepository userRepository,
                        TokenService tokenService,
                        EventPublisher eventPublisher,
                        PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.tokenService    = tokenService;
        this.eventPublisher  = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Ejecuta el login con los datos del LoginCommand.
     * Devuelve un LoginResult con el access token y refresh token.
     */
    public LoginResult execute(LoginCommand command) {

        // 1. Buscar el usuario por email
        User user = userRepository
                .findByEmail(new Email(command.email()))
                .orElseThrow(InvalidCredentialsException::new);
        // Nota: lanzamos InvalidCredentials (no UserNotFound) a propósito
        // — no queremos revelar si el email existe o no

        // 2. Verificar que la cuenta esté activa
        if (!user.isEnabled()) {
            throw new InvalidCredentialsException();
        }

        // 3. Verificar la contraseña contra el hash guardado
        boolean passwordMatches = passwordEncoder.matches(
                command.password(),
                user.getPassword().getValue()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        // 4. Generar los tokens
        String accessToken  = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);

        // 5. Publicar el evento de login (para auditoría, notificaciones, etc.)
        eventPublisher.publish(
                UserLoggedInEvent.of(user.getId(), user.getEmail().getValue(), command.ipAddress())
        );

        // 6. Devolver el resultado
        return new LoginResult(accessToken, refreshToken, user.getId().toString());
    }

    // ─── Resultado del login ──────────────────────────────
    public record LoginResult(
            String accessToken,
            String refreshToken,
            String userId
    ) {}

    // ─── Puerto interno para el encoder de contraseñas ───
    // Interface interna — Infrastructure inyecta BCryptPasswordEncoder
    public interface PasswordEncoder {
        boolean matches(String rawPassword, String encodedPassword);
        String  encode(String rawPassword);
    }
}