package ru.kotletkin.subatomics.deployments.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.subatomics.deployments.service.DeploymentService;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/admin/deployments")
@RequiredArgsConstructor
@Tag(name = "Admin Deployments API", description = "Administrative operations for deploy planes with services")
public class AdminDeploymentController {

    private final DeploymentService deployService;

    @DeleteMapping("/{name}")
    public void deletePlane(@PathVariable String name, @RequestHeader(name = "X-Action-Username") String actionUsername) {
        log.info("Удаление плана развертывания с именем: {} администратором: {}", name, actionUsername);
        deployService.deletePlane(name, actionUsername);
    }
}

