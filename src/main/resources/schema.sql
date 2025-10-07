-- Microservices registrations
CREATE TABLE IF NOT EXISTS microservices
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        TEXT                                NOT NULL,
    version     TEXT                                NOT NULL,
    image       TEXT                                NOT NULL,
    gitlab_link TEXT                                NOT NULL,
    author      TEXT                                NOT NULL,
    created_at  TIMESTAMP                           NOT NULL,
    updated_at  TIMESTAMP                           NOT NULL,
    CONSTRAINT uk_microservice_name_version UNIQUE (name, version)
);

CREATE TABLE IF NOT EXISTS microservice_environment_variables
(
    microservice_id BIGINT NOT NULL,
    variable_key    TEXT   NOT NULL,
    variable_value  TEXT   NOT NULL,
    PRIMARY KEY (microservice_id, variable_key),
    FOREIGN KEY (microservice_id) REFERENCES microservices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_microservices_created_at ON microservices (created_at);
CREATE INDEX IF NOT EXISTS idx_env_vars_microservice_id ON microservice_environment_variables (microservice_id);