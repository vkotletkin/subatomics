package ru.kotletkin.subatomics.deployments.dto.backend;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeployRequest {

    @NotBlank
    String namespace;

    Map<String, DeployModuleDTO> modules;
}
