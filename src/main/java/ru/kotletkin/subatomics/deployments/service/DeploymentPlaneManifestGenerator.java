package ru.kotletkin.subatomics.deployments.service;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;
import ru.kotletkin.subatomics.deployments.dto.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeploymentPlaneManifestGenerator {

    private final DeploymentsConfig deploymentsConfig;

    public Map<String, String> generatePlane(List<DeployModuleDTO> modulesInRequest, DeployRequest deployRequest,
                                             Map<Long, Registration> registrations) {

        Map<String, String> modulesManifestsMap = new HashMap<>();
        String namespace = deployRequest.getNamespace();

        for (DeployModuleDTO module : modulesInRequest) {
            String deploymentName = createDeploymentName(module);
            Deployment deployment = buildDeployment(module, namespace, deploymentName, registrations);
            String deploymentYaml = Serialization.asYaml(deployment);

            modulesManifestsMap.put(deploymentName, deploymentYaml);
        }

        return modulesManifestsMap;
    }

    private String createDeploymentName(DeployModuleDTO module) {
        return String.format("deployment-%d-%s-%d",
                module.getId(),
                module.getName(),
                System.currentTimeMillis());
    }

    private Deployment buildDeployment(DeployModuleDTO module, String namespace,
                                       String deploymentName, Map<Long, Registration> registrations) {

        return new DeploymentBuilder()
                .withNewMetadata()
                .withName(deploymentName)
                .withNamespace(namespace)
                .withLabels(createAppLabel(deploymentName))
                .endMetadata()
                .withNewSpec()
                .withReplicas(1)
                .withNewSelector()
                .withMatchLabels(createAppLabel(deploymentName))
                .endSelector()
                .withNewTemplate()
                .withNewMetadata()
                .withLabels(createAppLabel(deploymentName))
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("app")
                .withImage(getModuleImage(module, registrations))
                .withEnv(createEnvVars(module))
                .withResources(createResourceRequirements())
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();
    }

    private Map<String, String> createAppLabel(String deploymentName) {
        return Map.of("app", deploymentName);
    }

    private List<EnvVar> createEnvVars(DeployModuleDTO module) {
        return module.getEnvironments().entrySet().stream()
                .map(entry -> new EnvVarBuilder()
                        .withName(entry.getKey())
                        .withValue(entry.getValue())
                        .build())
                .toList();
    }

    private String getModuleImage(DeployModuleDTO module, Map<Long, Registration> registrations) {
        Registration registration = registrations.get(module.getId());
        if (registration == null) {
            throw new IllegalArgumentException("No registration found for module ID: " + module.getId());
        }
        return registration.getImage();
    }

    private ResourceRequirements createResourceRequirements() {

        ResourceRequirementsBuilder resourcesBuilder = new ResourceRequirementsBuilder();

        return resourcesBuilder
                .addToLimits(Map.of(
                        "cpu", new Quantity(deploymentsConfig.getParameters().getDefaults().getLimits().getCpu()),
                        "memory", new Quantity(deploymentsConfig.getParameters().getDefaults().getLimits().getMemory())
                ))
                .build();
    }
}
