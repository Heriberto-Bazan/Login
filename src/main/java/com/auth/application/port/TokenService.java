package com.auth.application.port;

import com.auth.domain.model.User;

/**
 * Puerto — define QUÉ operaciones necesita el UseCase para manejar tokens.
 *
 * El UseCase llama a esta interface sin saber que existe JJWT.
 * JwtProvider en Infrastructure es quien la implementa.
 *
 * Patrón Strategy — mañana puedes cambiar JJWT por otra librería
 * sin tocar ningún UseCase.
 */
public interface TokenService {

    /**
     * Genera un access token JWT para el usuario.
     * Expira en 15 minutos.
     */
    String generateAccessToken(User user);

    /**
     * Genera un refresh token para el usuario.
     * Expira en 7 días — se guarda en Redis.
     */
    String generateRefreshToken(User user);

    /**
     * Extrae el email del usuario desde un token JWT.
     * Lanza excepción si el token es inválido o expiró.
     */
    String extractEmail(String token);

    /**
     * Verifica si un token JWT es válido y no ha expirado.
     */
    boolean isTokenValid(String token);
}