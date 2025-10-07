package ru.kotletkin.subatomics.deployments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    String name;

    @NotNull
    Map<String, String> environments;

    DeployParametersDTO parameters;
}
