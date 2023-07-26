--liquibase formatted sql

--changeset bob:1

CREATE SCHEMA IF NOT EXISTS cadastro;

create table cadastro.relatorio
(
    id bigserial,
    backend_id bigint REFERENCES cadastro.backend (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE,
    arquivo        BYTEA        NOT NULL,
    tipo varchar(50) not null,
    nome varchar(100) not null,
    created_at TIMESTAMP(0) WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(0) WITHOUT TIME ZONE,
    primary key (id)
);