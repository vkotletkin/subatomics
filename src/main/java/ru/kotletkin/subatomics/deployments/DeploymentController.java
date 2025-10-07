package ru.kotletkin.subatomics.deployments;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.deployments.service.DeployServiceImpl;

@Validated
@RestController
@RequestMapping("/api/v1/deployments")
@RequiredArgsConstructor
public class DeploymentController {

    private final DeployServiceImpl deployService;

    @PostMapping
    public void deploySchema(@Valid @RequestBody DeployRequest deployRequest) {
        deployService.handleRequest(deployRequest);
    }
}
