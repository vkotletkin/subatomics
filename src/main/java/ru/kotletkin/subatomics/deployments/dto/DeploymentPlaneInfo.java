package ru.kotletkin.subatomics.deployments.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.kotletkin.subatomics.common.dto.CatalogDTO;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeploymentPlaneInfo {
    String deploymentPlane;
    List<CatalogDTO> modules;
}
