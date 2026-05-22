package com.auth.domain.exception;

/**
 * Se lanza cuando el email o contraseña son incorrectos.
 * El Controller la convierte en HTTP 401 Unauthorized.
 *
 * IMPORTANTE: el mensaje es genérico a propósito — no decimos
 * "email incorrecto" ni "contraseña incorrecta" por seguridad.
 * Un atacante no debe saber cuál de los dos falló.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}