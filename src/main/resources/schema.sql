-- Microservices registrations
CREATE TABLE IF NOT EXISTS registration_services
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        TEXT                                NOT NULL,
    version     TEXT                                NOT NULL,
    image       TEXT                                NOT NULL,
    gitlab_link TEXT                                NOT NULL,
    author      TEXT                                NOT NULL,
    created_at  TIMESTAMP                           NOT NULL,
    updated_at  TIMESTAMP                           NOT NULL,
    CONSTRAINT uk_registration_name_version UNIQUE (name, version)
);

CREATE TABLE IF NOT EXISTS registration_services_environments
(
    registration_id BIGINT NOT NULL,
    variable_key    TEXT   NOT NULL,
    variable_value  TEXT   NOT NULL,
    PRIMARY KEY (registration_id, variable_key),
    FOREIGN KEY (registration_id) REFERENCES registration_services (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_registrations_created_at ON registration_services (created_at);
CREATE INDEX IF NOT EXISTS idx_env_vars_registration_id ON registration_services_environments (registration_id);
