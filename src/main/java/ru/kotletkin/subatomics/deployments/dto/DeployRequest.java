package ru.kotletkin.subatomics.deployments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeployRequest {

    @NotBlank
    String deployName;

    @NotBlank
    String namespace;

    @NotBlank
    String requesterName;

    List<DeployModuleDTO> modules;
}
