package com.auth.domain.model;

import java.util.Objects;

/**
 * Value Object — representa una contraseña que cumple las reglas de negocio.
 *
 * Reglas:
 *  - Mínimo 8 caracteres
 *  - Al menos una mayúscula
 *  - Al menos una minúscula
 *  - Al menos un número
 *  - Al menos un caracter especial
 *
 * IMPORTANTE: este VO guarda el valor en texto plano ANTES del hash.
 * El hash lo aplica JwtProvider en Infrastructure — no es responsabilidad del Domain.
 */
public final class Password {

    private final String value;

    public Password(String value) {
        Objects.requireNonNull(value, "La contraseña no puede ser nula");
        validate(value);
        this.value = value;
    }

    /**
     * Crea un Password desde un hash ya almacenado en base de datos.
     * No aplica validación de reglas porque ya está hasheada.
     */
    public static Password fromHash(String hash) {
        Objects.requireNonNull(hash, "El hash no puede ser nulo");
        return new Password(hash, true);
    }

    // Constructor privado para hash — bypasea validación
    private Password(String value, boolean isHash) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // ─── Validación de reglas de negocio ──────────────────
    private void validate(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos una mayúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos una minúscula");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos un número");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos un carácter especial");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Password other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "***";
    }
}