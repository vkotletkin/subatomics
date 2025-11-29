package ru.kotletkin.aard.deployments.service;

import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kotletkin.aard.deployments.dto.DeploymentPlaneEnrichedDTO;
import ru.kotletkin.aard.deployments.dto.backend.DeployModuleDTO;
import ru.kotletkin.aard.deployments.dto.backend.DeployRequest;
import ru.kotletkin.aard.registration.RegistrationRepository;
import ru.kotletkin.aard.registration.model.Registration;

import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    public void deployPlane(DeploymentPlaneEnrichedDTO deploymentPlaneEnrichedDTO, String actionUsername) {

        String deployPlaneName = deploymentPlaneEnrichedDTO.getPlaneName();

        DeployRequest deployRequest = deploymentPlaneEnrichedDTO.getBackend();
        // check query validate
        deploymentPlaneRequestValidator.validate(deployRequest);

        Map<UUID, DeployModuleDTO> modulesInRequest = deployRequest.getModules();

        List<Long> moduleIds = modulesInRequest.values().stream().map(DeployModuleDTO::getModuleRegistrationId).distinct().toList();
        Map<Long, Registration> registrations = registrationRepository.findByIdIn(moduleIds).stream()
                .collect(Collectors.toMap(Registration::getId, Function.identity()));

        // check all modules are really exists in registrations and environments equals
        deploymentPlaneRequestValidator.validateModulesConsistency(modulesInRequest.values(), registrations);

        // create manifests for k8s
        Map<String, String> deployments = deploymentPlaneManifestGenerator.generatePlane(modulesInRequest, deployRequest, registrations);

        String coreSchemaBody = Serialization.asJson(deploymentPlaneEnrichedDTO);

        gitlabProjectService.createDeploy(coreSchemaBody, deployPlaneName, actionUsername, deployments);
    }

    public List<DeploymentPlaneEnrichedDTO> findAllPlanes() {
        return gitlabProjectService.findAllDeployments();
    }

    public void deletePlane(String name, String actionUsername) {
        gitlabProjectService.deleteDeployment(actionUsername, name);
    }
}
