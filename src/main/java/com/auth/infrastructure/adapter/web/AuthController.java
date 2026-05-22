package com.auth.infrastructure.adapter.web;

import com.auth.application.command.LoginCommand;
import com.auth.application.command.RegisterCommand;
import com.auth.application.usecase.LoginUseCase;
import com.auth.application.usecase.RegisterUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada — recibe requests HTTP y los convierte en Commands.
 *
 * Responsabilidades:
 *  - Recibir y validar el request HTTP (@Valid)
 *  - Convertir el request en un Command
 *  - Llamar al UseCase correspondiente
 *  - Convertir el resultado en una respuesta HTTP
 *
 * NO contiene lógica de negocio — eso vive en los UseCases.
 * SRP — solo traduce HTTP ↔ Application.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase    loginUseCase;
    private final RegisterUseCase registerUseCase;

    // Inyección por constructor — sin @Autowired (buena práctica)
    public AuthController(LoginUseCase loginUseCase,
                          RegisterUseCase registerUseCase) {
        this.loginUseCase    = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    // ─── POST /api/v1/auth/login ──────────────────────────
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        // 1. Convertir request HTTP en Command
        LoginCommand command = new LoginCommand(
                request.email(),
                request.password(),
                httpRequest.getRemoteAddr()   // IP del cliente
        );

        // 2. Ejecutar el UseCase
        LoginUseCase.LoginResult result = loginUseCase.execute(command);

        // 3. Devolver respuesta HTTP 200
        return ResponseEntity.ok(new LoginResponse(
                result.accessToken(),
                result.refreshToken(),
                result.userId()
        ));
    }

    // ─── POST /api/v1/auth/register ───────────────────────
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        // 1. Convertir request HTTP en Command
        RegisterCommand command = new RegisterCommand(
                request.email(),
                request.password(),
                request.confirmPassword()
        );

        // 2. Ejecutar el UseCase
        RegisterUseCase.RegisterResult result = registerUseCase.execute(command);

        // 3. Devolver respuesta HTTP 201 Created
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterResponse(result.userId(), result.email()));
    }

    // ─── DTOs de entrada (Request) ────────────────────────

    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Formato de email inválido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            String password
    ) {}

    public record RegisterRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Formato de email inválido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
            String password,

            @NotBlank(message = "La confirmación de contraseña es obligatoria")
            String confirmPassword
    ) {}

    // ─── DTOs de salida (Response) ────────────────────────

    public record LoginResponse(
            String accessToken,
            String refreshToken,
            String userId
    ) {}

    public record RegisterResponse(
            String userId,
            String email
    ) {}
}