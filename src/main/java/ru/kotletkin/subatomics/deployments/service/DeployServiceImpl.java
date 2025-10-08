package ru.kotletkin.subatomics.deployments.service;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;
import ru.kotletkin.subatomics.common.exception.IncorrectModulesException;
import ru.kotletkin.subatomics.deployments.dto.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployParametersDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.deployments.model.DeploymentPlane;
import ru.kotletkin.subatomics.deployments.repository.DeploymentPlaneRepository;
import ru.kotletkin.subatomics.registration.Registration;
import ru.kotletkin.subatomics.registration.RegistrationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployServiceImpl {

    private final RegistrationRepository registrationRepository;
    private final DeploymentsConfig deploymentsConfig;
    private final DeploymentPlaneRepository deploymentPlaneRepository;

    public void handleRequest(DeployRequest deployRequest) {

        // add deployName exists or not
        if (deploymentPlaneRepository.findByNamespace(deployRequest.getNamespace()).size() != 0) {
        }
        // add check namespace with modules or not

        // check query validate
        validateRequest(deployRequest);

        List<DeployModuleDTO> modulesInRequest = deployRequest.getModules();

        List<Long> modulesNames = modulesInRequest.stream().map(DeployModuleDTO::getId).distinct().toList();
        Map<Long, Registration> registrations = registrationRepository.findByIdIn(modulesNames).stream()
                .collect(Collectors.toMap(Registration::getId, Function.identity()));

        // check size consistency of all modules in requests exists
        checkSize(registrations, modulesNames);

        // check all modules are really exists in registrations and environments equals
        checkModulesConsistency(modulesInRequest, registrations);

        DeploymentPlane deploymentPlane = generatePlane(modulesInRequest, deployRequest, registrations);

        deploymentPlaneRepository.save(deploymentPlane);
    }

    private void validateRequest(DeployRequest request) {

        if (request.getModules() == null || request.getModules().isEmpty()) {
            throw new IllegalArgumentException("Modules list cannot be empty");
        }

        request.getModules().forEach(module -> {

            if (module.getParameters() == null) {
                DeployParametersDTO parameters = new DeployParametersDTO();
                parameters.setCpuLimit(deploymentsConfig.getParameters().getDefaults().getLimits().getCpu());
                parameters.setMemoryLimit(deploymentsConfig.getParameters().getDefaults().getLimits().getMemory());
                parameters.setLivenessProbe(deploymentsConfig.getParameters().getDefaults().getProbes().getLiveness());
                parameters.setReadinessProbe(deploymentsConfig.getParameters().getDefaults().getProbes().getReadiness());
                module.setParameters(parameters);
            }

        });
    }

    private void checkSize(Map<Long, Registration> registrations, List<Long> modulesNames) {
        if (registrations.size() != modulesNames.stream().toList().size()) {
            throw new IncorrectModulesException("Modules names and registrations do not match");
        }
    }

    private void checkModulesConsistency(List<DeployModuleDTO> modulesInRequest, Map<Long, Registration> registrations) {

        for (DeployModuleDTO module : modulesInRequest) {

            Registration registration = registrations.get(module.getId());

            if (registration == null) {
                throw new IncorrectModulesException("Module {0} not found", module.getId());
            }

            if (!registration.getEnvironmentVariables().equals(module.getEnvironments())) {
                throw new IncorrectModulesException("Environment variables do not match in modules");
            }

        }
    }

    private DeploymentPlane generatePlane(List<DeployModuleDTO> modulesInRequest, DeployRequest deployRequest,
                                          Map<Long, Registration> registrations) {

        Map<String, String> modulesManifestsMap = new HashMap<>();

        for (DeployModuleDTO module : modulesInRequest) {

            String name = "deployment-" + module.getId() + "-" + System.currentTimeMillis();

            List<EnvVar> envVars = new ArrayList<>();
            for (Map.Entry<String, String> keyVal : module.getEnvironments().entrySet()) {
                EnvVar envVar = new EnvVarBuilder()
                        .withName(keyVal.getKey())
                        .withValue(keyVal.getValue())
                        .build();
                envVars.add(envVar);
            }

            Deployment deployment = new DeploymentBuilder()
                    .withNewMetadata()
                    .withName(name)
                    .withNamespace(deployRequest.getNamespace())
                    .withLabels(Map.of("app", name))
                    .endMetadata()
                    .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector()
                    .withMatchLabels(Map.of("app", name))
                    .endSelector()
                    .withNewTemplate()
                    .withNewMetadata()
                    .withLabels(Map.of("app", name))
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName("app")
                    .withImage(registrations.get(module.getId()).getImage())
                    .withEnv(envVars)
                    .withNewResources()
                    .addToLimits(Map.of(
                            "cpu", new Quantity(deploymentsConfig.getParameters().getDefaults().getLimits().getCpu()),
                            "memory", new Quantity(deploymentsConfig.getParameters().getDefaults().getLimits().getMemory())
                    ))
                    .endResources()
                    .endContainer()
                    .endSpec()
                    .endTemplate()
                    .endSpec()
                    .build();

            modulesManifestsMap.put(name, Serialization.asYaml(deployment));

        }
        return DeploymentPlane.builder()
                .name(deployRequest.getDeployName())
                .namespace(deployRequest.getNamespace())
                .requesterName(deployRequest.getRequesterName())
                .modulesManifestMap(modulesManifestsMap)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
