package ru.kotletkin.subatomics.deployments.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.subatomics.deployments.dto.DeploymentPlaneEnrichedDTO;
import ru.kotletkin.subatomics.deployments.service.DeploymentService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
@Tag(name = "Public Deployments API", description = "Public operations for deploy planes with services")
public class PublicDeploymentController {

    private final DeploymentService deployService;

    @GetMapping
    public List<DeploymentPlaneEnrichedDTO> getAll() {
        return deployService.findAllPlanes();
    }

    @PostMapping
    public void deployPlane(@Valid @RequestBody DeploymentPlaneEnrichedDTO deploymentPlaneEnrichedDTO,
                            @RequestHeader(name = "X-Action-Username") String actionUsername) {
        deployService.deployPlane(deploymentPlaneEnrichedDTO, actionUsername);
    }
}
