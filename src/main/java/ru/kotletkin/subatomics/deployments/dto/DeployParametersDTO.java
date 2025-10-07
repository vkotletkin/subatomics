package ru.kotletkin.subatomics.deployments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeployParametersDTO {

    String cpuLimit;
    String memoryLimit;
    String livenessProbe;
    String readinessProbe;
}
