package ru.kotletkin.aard.deployments.dto.backend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeployModuleDTO {

    @NotBlank
    Long moduleRegistrationId;

    @NotNull
    String name;

    @NotNull
    Map<String, String> environments;

    DeployParametersDTO parameters;
}
