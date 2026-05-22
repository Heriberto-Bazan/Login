package com.auth.application.command;

/**
 * Command — transporta los datos necesarios para registrar un usuario nuevo.
 *
 * Similar a LoginCommand pero con un campo extra: confirmPassword.
 * La validación de que ambas contraseñas coincidan la hace RegisterUseCase.
 */
public record RegisterCommand(
        String email,
        String password,
        String confirmPassword
) {
    // Validación compacta — se ejecuta en el constructor
    public RegisterCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("La confirmación de contraseña no puede estar vacía");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
    }
}