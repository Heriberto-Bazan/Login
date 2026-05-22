package com.auth.infrastructure.adapter.security;

import com.auth.application.port.TokenService;
import com.auth.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Adaptador de seguridad — implementa el puerto TokenService con JJWT.
 *
 * Genera y valida tokens JWT.
 * El UseCase solo conoce la interface TokenService —
 * no sabe que existe JJWT ni este archivo.
 *
 * Patrón Strategy — si mañana cambias de JJWT a otra librería,
 * solo cambias este archivo. Los UseCases no se tocan.
 */
@Component
public class JwtProvider implements TokenService {

    private final SecretKey secretKey;
    private final long      accessTokenExpiration;
    private final long      refreshTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration-ms}") long refreshTokenExpiration) {

        this.secretKey              = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration  = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    // ─── Genera el access token (15 minutos) ──────────────
    @Override
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Incluimos los roles en el token para que otros servicios
        // puedan verificar permisos sin consultar la base de datos
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList()));
        claims.put("userId", user.getId().toString());

        return buildToken(claims, user.getEmail().getValue(), accessTokenExpiration);
    }

    // ─── Genera el refresh token (7 días) ─────────────────
    @Override
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user.getEmail().getValue(), refreshTokenExpiration);
    }

    // ─── Extrae el email del token ─────────────────────────
    @Override
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // ─── Verifica si el token es válido ───────────────────
    @Override
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // ─── Métodos privados de soporte ──────────────────────

    private String buildToken(Map<String, Object> extraClaims,
                              String subject,
                              long expiration) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}