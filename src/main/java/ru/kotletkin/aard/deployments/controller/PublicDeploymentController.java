package ru.kotletkin.aard.deployments.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.aard.deployments.dto.DeploymentPlaneEnrichedDTO;
import ru.kotletkin.aard.deployments.service.DeploymentService;

import java.util.List;

@Slf4j
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

    @DeleteMapping("/{name}")
    public void deletePlane(@PathVariable String name, @RequestHeader(name = "X-Action-Username") String actionUsername) {
        log.info("Удаление плана развертывания с именем: {} администратором: {}", name, actionUsername);
        deployService.deletePlane(name, actionUsername);
    }
}
