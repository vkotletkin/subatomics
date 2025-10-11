package ru.kotletkin.subatomics.deployments.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeployParametersDTO {
    String cpuLimit;
    String memoryLimit;
    String livenessProbe;
    String readinessProbe;
}
