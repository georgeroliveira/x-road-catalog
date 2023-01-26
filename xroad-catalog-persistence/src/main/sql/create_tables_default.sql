-- noinspection SqlNoDataSourceInspectionForFile
-- Create tables

\connect xroad_catalog;

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

CREATE TABLE IF NOT EXISTS member (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    x_road_instance TEXT NOT NULL,
    member_class TEXT NOT NULL,
    member_code TEXT NOT NULL,
    name TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
);

CREATE SEQUENCE IF NOT EXISTS member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE member_id_seq OWNED BY member.id;

CREATE TABLE IF NOT EXISTS subsystem (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    member_id BIGSERIAL NOT NULL REFERENCES member(id),
    subsystem_code TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
);

CREATE SEQUENCE IF NOT EXISTS subsystem_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE subsystem_id_seq OWNED BY subsystem.id;

CREATE TABLE IF NOT EXISTS service (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    subsystem_id BIGSERIAL NOT NULL REFERENCES subsystem(id),
    service_code TEXT NOT NULL,
    service_version TEXT,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
);

CREATE SEQUENCE IF NOT EXISTS service_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE service_id_seq OWNED BY service.id;

CREATE TABLE IF NOT EXISTS wsdl (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    service_id BIGSERIAL NOT NULL REFERENCES service(id),
    data TEXT NOT NULL,
    external_id TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
);

CREATE SEQUENCE IF NOT EXISTS wsdl_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE wsdl_id_seq OWNED BY wsdl.id;

CREATE SEQUENCE IF NOT EXISTS wsdl_external_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE wsdl_external_id_seq OWNED BY wsdl.external_id;

CREATE TABLE IF NOT EXISTS open_api (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    service_id BIGSERIAL NOT NULL REFERENCES service(id),
    data TEXT NOT NULL,
    external_id TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
);

CREATE SEQUENCE IF NOT EXISTS open_api_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE open_api_id_seq OWNED BY open_api.id;

CREATE SEQUENCE IF NOT EXISTS open_api_external_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

ALTER SEQUENCE open_api_external_id_seq OWNED BY open_api.external_id;

CREATE TABLE IF NOT EXISTS rest (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    service_id BIGSERIAL NOT NULL REFERENCES service(id),
    data TEXT NOT NULL,
    external_id TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
                          );

CREATE SEQUENCE IF NOT EXISTS rest_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE rest_id_seq OWNED BY rest.id;

CREATE SEQUENCE IF NOT EXISTS rest_external_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

ALTER SEQUENCE rest_external_id_seq OWNED BY rest.external_id;

CREATE TABLE IF NOT EXISTS endpoint (
                                    id BIGSERIAL PRIMARY KEY NOT NULL,
                                    service_id BIGSERIAL NOT NULL REFERENCES service(id),
    method TEXT NOT NULL,
    path TEXT NOT NULL,
    created TIMESTAMP WITH TIME ZONE NOT NULL,
    changed TIMESTAMP WITH TIME ZONE NOT NULL,
    fetched TIMESTAMP WITH TIME ZONE NOT NULL,
    removed TIMESTAMP WITH TIME ZONE
                          );

CREATE SEQUENCE IF NOT EXISTS endpoint_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE endpoint_id_seq OWNED BY endpoint.id;

CREATE TABLE IF NOT EXISTS error_log (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    message TEXT NOT NULL,
    code TEXT NOT NULL,
    x_road_instance TEXT,
    member_class TEXT,
    member_code TEXT,
    subsystem_code TEXT,
    group_code TEXT,
    service_code TEXT,
    service_version TEXT,
    security_category_code TEXT,
    server_code TEXT,
    created TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS error_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE error_log_id_seq OWNED BY error_log.id;

CREATE UNIQUE INDEX IF NOT EXISTS idx_wsdl_external_id ON wsdl USING btree (external_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_open_api_external_id ON open_api USING btree (external_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_rest_external_id ON rest USING btree (external_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_member_natural_keys ON member(member_code, member_class, x_road_instance);
CREATE UNIQUE INDEX IF NOT EXISTS idx_service_unique_fields ON service(subsystem_id, service_code, service_version);
CREATE UNIQUE INDEX IF NOT EXISTS idx_subsystem_unique_fields ON subsystem(member_id, subsystem_code);

CREATE INDEX IF NOT EXISTS idx_wsdl_changed ON wsdl(changed);
CREATE INDEX IF NOT EXISTS idx_open_api_changed ON open_api(changed);
CREATE INDEX IF NOT EXISTS idx_rest_changed ON rest(changed);
CREATE INDEX IF NOT EXISTS idx_endpoint_changed ON endpoint(changed);
CREATE INDEX IF NOT EXISTS idx_service_changed ON service(changed);
CREATE INDEX IF NOT EXISTS idx_subsystem_changed ON subsystem(changed);
CREATE INDEX IF NOT EXISTS idx_member_changed ON member(changed);

ALTER TABLE member OWNER TO xroad_catalog;
ALTER TABLE service OWNER TO xroad_catalog;
ALTER TABLE subsystem OWNER TO xroad_catalog;
ALTER TABLE wsdl OWNER TO xroad_catalog;
ALTER TABLE open_api OWNER TO xroad_catalog;
ALTER TABLE rest OWNER TO xroad_catalog;
ALTER TABLE endpoint OWNER TO xroad_catalog;
ALTER TABLE error_log OWNER TO xroad_catalog;