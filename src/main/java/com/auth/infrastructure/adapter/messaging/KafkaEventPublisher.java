package com.auth.infrastructure.adapter.messaging;

import com.auth.application.port.EventPublisher;
import com.auth.domain.event.UserLoggedInEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de mensajería — implementa el puerto EventPublisher con Kafka.
 *
 * El UseCase llama a EventPublisher.publish() sin saber que existe Kafka.
 * Este archivo es el único que conoce KafkaTemplate.
 *
 * Patrón Observer — otros servicios suscritos al topic reciben el evento:
 *  - Servicio de auditoría
 *  - Servicio de notificaciones
 *  - Servicio de analytics
 */
@Component
public class KafkaEventPublisher implements EventPublisher {

    private static final String LOGIN_TOPIC = "auth.user.logged-in";

    private final KafkaTemplate<String, UserLoggedInEvent> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, UserLoggedInEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(UserLoggedInEvent event) {
        // La clave del mensaje es el userId — garantiza orden por usuario
        kafkaTemplate.send(LOGIN_TOPIC, event.userId().toString(), event);
    }
}