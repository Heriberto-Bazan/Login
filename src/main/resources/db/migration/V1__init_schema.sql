-- V1__init_schema.sql
-- Schema inicial: users, roles, refresh_tokens

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ─── Tabla de roles ──────────────────────────────────────────
CREATE TABLE roles (
    id      UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name    VARCHAR(50)  NOT NULL UNIQUE  -- ROLE_USER, ROLE_ADMIN
);

-- ─── Tabla de usuarios ───────────────────────────────────────
CREATE TABLE users (
    id           UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    email        VARCHAR(255)  NOT NULL UNIQUE,
    password     VARCHAR(255)  NOT NULL,
    enabled      BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ─── Relación usuarios-roles (muchos a muchos) ────────────────
CREATE TABLE user_roles (
    user_id  UUID REFERENCES users(id) ON DELETE CASCADE,
    role_id  UUID REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ─── Refresh tokens ──────────────────────────────────────────
-- Respaldo en DB por si Redis cae; Redis es la fuente primaria
CREATE TABLE refresh_tokens (
    id          UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id     UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(512) NOT NULL UNIQUE,
    expires_at  TIMESTAMP    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- ─── Datos iniciales ─────────────────────────────────────────
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');
