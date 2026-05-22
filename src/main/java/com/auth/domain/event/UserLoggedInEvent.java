package com.auth.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio — se publica cuando un usuario hace login exitoso.
 *
 * Kafka lo recibe en Infrastructure y lo distribuye.
 * El Domain solo define QUÉ pasó — no sabe nada de Kafka.
 *
 * Otros servicios pueden reaccionar a este evento:
 *  - Auditoría: registrar el acceso
 *  - Notificaciones: email de "nuevo acceso detectado"
 *  - Analytics: conteo de logins
 */
public record UserLoggedInEvent(
        UUID          userId,
        String        email,
        LocalDateTime occurredAt,
        String        ipAddress
) {
    public static UserLoggedInEvent of(UUID userId, String email, String ipAddress) {
        return new UserLoggedInEvent(userId, email, LocalDateTime.now(), ipAddress);
    }
}