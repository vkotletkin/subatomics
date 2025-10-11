package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;
import ru.kotletkin.subatomics.deployments.dto.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.registration.RegistrationRepository;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentServiceImpl {

    private final DeploymentPlaneRequestValidator deploymentPlaneRequestValidator;
    private final DeploymentPlaneManifestGenerator deploymentPlaneManifestGenerator;
    private final GitlabProjectServiceImpl gitlabProjectService;
    private final RegistrationRepository registrationRepository;

    public void handleRequest(DeployRequest deployRequest) {

        // check query validate
        deploymentPlaneRequestValidator.validate(deployRequest);

        List<DeployModuleDTO> modulesInRequest = deployRequest.getModules();

        List<Long> modulesNames = modulesInRequest.stream().map(DeployModuleDTO::getId).distinct().toList();
        Map<Long, Registration> registrations = registrationRepository.findByIdIn(modulesNames).stream()
                .collect(Collectors.toMap(Registration::getId, Function.identity()));

        // check size consistency of all modules in requests exists
        deploymentPlaneRequestValidator.validateSize(registrations, modulesNames);

        // check all modules are really exists in registrations and environments equals
        deploymentPlaneRequestValidator.validateModulesConsistency(modulesInRequest, registrations);

        // create manifests for k8s
        Map<String, String> deployments = deploymentPlaneManifestGenerator.generatePlane(modulesInRequest, deployRequest, registrations);


        try {
            gitlabProjectService.createDeploy(deployRequest.getDeployName(), deployRequest.getRequesterName(), deployments);
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }

    }
}
