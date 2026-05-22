package com.auth.application.port;

import com.auth.domain.event.UserLoggedInEvent;

/**
 * Puerto — define QUÉ eventos puede publicar el UseCase.
 *
 * El UseCase llama a esta interface sin saber que existe Kafka.
 * KafkaEventPublisher en Infrastructure es quien la implementa.
 *
 * Patrón Observer — cuando ocurre un login, otros servicios
 * pueden reaccionar al evento sin que el UseCase los conozca.
 */
public interface EventPublisher {

    /**
     * Publica el evento cuando un usuario hace login exitoso.
     * Kafka lo distribuye a los servicios suscritos.
     */
    void publish(UserLoggedInEvent event);
}