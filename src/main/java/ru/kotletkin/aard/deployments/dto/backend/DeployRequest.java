package ru.kotletkin.aard.deployments.dto.backend;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeployRequest {

    @NotBlank
    String namespace;

    Map<UUID, DeployModuleDTO> modules;
}
