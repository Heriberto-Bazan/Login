package com.auth.domain.exception;

import java.util.UUID;

/**
 * Se lanza cuando se busca un usuario que no existe.
 * El Controller en Infrastructure la convierte en HTTP 404.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("Usuario no encontrado con id: " + id);
    }

    public UserNotFoundException(String email) {
        super("Usuario no encontrado con email: " + email);
    }
}