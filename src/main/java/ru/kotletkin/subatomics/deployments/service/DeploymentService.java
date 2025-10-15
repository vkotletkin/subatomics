package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.deployments.dto.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.deployments.dto.DeploymentPlaneInfo;
import ru.kotletkin.subatomics.registration.RegistrationRepository;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final DeploymentPlaneRequestValidator deploymentPlaneRequestValidator;
    private final DeploymentPlaneManifestGenerator deploymentPlaneManifestGenerator;
    private final GitlabProjectService gitlabProjectService;
    private final RegistrationRepository registrationRepository;

    public void deployPlane(DeployRequest deployRequest, String actionUsername) {

        // check query validate
        deploymentPlaneRequestValidator.validate(deployRequest);

        List<DeployModuleDTO> modulesInRequest = deployRequest.getModules();

        List<Long> modulesNames = modulesInRequest.stream().map(DeployModuleDTO::getId).distinct().toList();
        Map<Long, Registration> registrations = registrationRepository.findByIdIn(modulesNames).stream()
                .collect(Collectors.toMap(Registration::getId, Function.identity()));

        // check all modules are really exists in registrations and environments equals
        deploymentPlaneRequestValidator.validateModulesConsistency(modulesInRequest, registrations);

        // create manifests for k8s
        Map<String, String> deployments = deploymentPlaneManifestGenerator.generatePlane(modulesInRequest, deployRequest, registrations);

        gitlabProjectService.createDeploy(deployRequest.getDeploymentPlaneName(), actionUsername, deployments);
    }

    public List<DeploymentPlaneInfo> findAllDeployments() {
        return gitlabProjectService.findAllDeployments();
    }

    public void deletePlane(String name, String actionUsername) {
        gitlabProjectService.deleteDeployment(actionUsername, name);
    }
}
