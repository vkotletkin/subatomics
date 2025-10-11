package ru.kotletkin.subatomics.deployments;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.deployments.dto.DeploymentPlaneInfo;
import ru.kotletkin.subatomics.deployments.service.DeploymentService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeploymentService deployService;

    @GetMapping
    public List<DeploymentPlaneInfo> getAll() {
        return deployService.findAllDeployments();
    }

    @PostMapping
    public void deploySchema(@Valid @RequestBody DeployRequest deployRequest) {
        deployService.handleRequest(deployRequest);
    }
}
