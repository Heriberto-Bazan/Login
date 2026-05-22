package com.auth.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object — representa un email válido.
 *
 * Reglas:
 *  - Inmutable (no tiene setters)
 *  - Se valida en el constructor — nunca existe un Email inválido
 *  - Igualdad por valor, no por referencia
 *
 * Sin anotaciones de Spring ni JPA — Java puro.
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    public Email(String value) {
        Objects.requireNonNull(value, "El email no puede ser nulo");

        String trimmed = value.trim().toLowerCase();

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Formato de email inválido: " + value);
        }

        this.value = trimmed;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}