package com.auth.application.command;

/**
 * Command — transporta los datos necesarios para ejecutar un login.
 *
 * Reglas de un Command:
 *  - Solo carga datos, no hace nada por sí solo
 *  - Inmutable — nadie puede modificarlo después de crearlo
 *  - Un Command por acción: LoginCommand solo sirve para login
 *
 * Usamos record de Java 21 — genera automáticamente:
 *  - Constructor
 *  - Getters
 *  - equals(), hashCode(), toString()
 */
public record LoginCommand(
        String email,
        String password,
        String ipAddress    // para el evento de auditoría
) {
    // Validación compacta del record — se ejecuta en el constructor
    public LoginCommand {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
    }
}