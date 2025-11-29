package ru.kotletkin.subatomics.deployments.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.kotletkin.subatomics.deployments.dto.backend.DeployRequest;
import ru.kotletkin.subatomics.deployments.dto.frontend.FrontendTempDTO;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeploymentPlaneEnrichedDTO {

    @NotEmpty
    String planeName;

    @NotNull
    List<FrontendTempDTO> frontend;

    @NotNull
    DeployRequest backend;
}
