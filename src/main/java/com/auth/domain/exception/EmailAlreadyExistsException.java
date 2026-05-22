package com.auth.domain.exception;

/**
 * Se lanza cuando se intenta registrar un email que ya existe.
 * El Controller en Infrastructure la convierte en HTTP 409 Conflict.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("El email ya está registrado: " + email);
    }
}