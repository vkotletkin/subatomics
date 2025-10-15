package ru.kotletkin.subatomics.deployments.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/admin/deployments")
@RequiredArgsConstructor
@Tag(name = "Public Deployments API", description = "Public operations for deploy planes with services")
public class PublicDeploymentController {

    private final DeploymentService deployService;

    @GetMapping
    public List<DeploymentPlaneInfo> getAll() {
        return deployService.findAllDeployments();
    }

    @PostMapping
    public void deployPlane(@Valid @RequestBody DeployRequest deployRequest, @RequestHeader(name = "X-Action-Username") String actionUsername) {
        deployService.deployPlane(deployRequest, actionUsername);
    }
}
