-- Script de criação das tabelas em Produção (PostgreSQL)

-- Tabela pai de credenciais (Estratégia SINGLE_TABLE)
CREATE TABLE IF NOT EXISTS credentials (
    id BIGSERIAL PRIMARY KEY,
    auth_type VARCHAR(31) NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    tokenUrl VARCHAR(255),
    clientId VARCHAR(255),
    clientSecret VARCHAR(255)
);

-- Tabela de fontes de dados (Sources)
CREATE TABLE IF NOT EXISTS sources (
    id VARCHAR(255) PRIMARY KEY,
    uri VARCHAR(255) NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    credential_id BIGINT UNIQUE,
    CONSTRAINT fk_source_credential FOREIGN KEY (credential_id) REFERENCES credentials(id) ON DELETE CASCADE
);

-- Tabela para coleção de Client IDs permitidos para Fontes privadas
CREATE TABLE IF NOT EXISTS source_allowed_clients (
    source_id VARCHAR(255) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_source_allowed_clients_source FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE CASCADE
);
