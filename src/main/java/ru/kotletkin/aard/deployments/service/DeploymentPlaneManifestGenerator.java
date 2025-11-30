package ru.kotletkin.aard.deployments.service;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.utils.Serialization;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotletkin.aard.common.config.DeploymentsConfig;
import ru.kotletkin.aard.common.exception.ModuleExistException;
import ru.kotletkin.aard.deployments.dto.backend.DeployModuleDTO;
import ru.kotletkin.aard.deployments.dto.backend.DeployRequest;
import ru.kotletkin.aard.registration.model.Registration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeploymentPlaneManifestGenerator {

    private static final String DEPLOYMENT_NAME_PATTERN = "deployment-%d-%s-%s"; // DEPLOYMENT-REGISTRATIONID-UUID-NAME
    private static final String NAMESPACE_NAME_PATTERN = "namespace-%s";

    private static final String IMAGE_PULLPOLICY_SETTING_VALUE = "Always";

    private final DeploymentsConfig deploymentsConfig;

    public Map<String, String> generatePlane(Map<UUID, DeployModuleDTO> modulesInRequest, DeployRequest deployRequest,
                                             Map<Long, Registration> registrations) {

        Map<String, String> modulesManifestsMap = new HashMap<>();
        String namespaceName = deployRequest.getNamespace();

        Namespace namespace = buildNamespace(namespaceName);
        modulesManifestsMap.put(createNamespaceName(namespaceName), Serialization.asYaml(namespace));
        modulesManifestsMap.putAll(generateModuleManifests(modulesInRequest, namespaceName, registrations));

        return modulesManifestsMap;
    }

    public Map<String, String> generateModuleManifests(Map<UUID, DeployModuleDTO> modules, String namespaceName,
                                                       Map<Long, Registration> registrations) {
        Map<String, String> manifests = new HashMap<>();
        for (Map.Entry<UUID, DeployModuleDTO> moduleEntry : modules.entrySet()) {
            String moduleName = moduleEntry.getKey().toString().replace("-", ""); // uuid to string
            DeployModuleDTO module = moduleEntry.getValue();
            String deploymentName = createDeploymentName(moduleName, module);
            Deployment deployment = buildDeployment(module, namespaceName, deploymentName, registrations);
            String deploymentYaml = Serialization.asYaml(deployment);

            manifests.put(deploymentName, deploymentYaml);
        }
        return manifests;
    }


    private String createDeploymentName(String name, DeployModuleDTO module) {
        return String.format(DEPLOYMENT_NAME_PATTERN,
                module.getModuleRegistrationId(),
                name,
                module.getName());
    }

    private String createNamespaceName(String namespaceName) {
        return String.format(NAMESPACE_NAME_PATTERN, namespaceName);
    }

    private Namespace buildNamespace(String namespace) {
        return new NamespaceBuilder().withNewMetadata().withName(namespace).withLabels(Map.of("name", namespace)).endMetadata().build();
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
                .withImagePullPolicy(IMAGE_PULLPOLICY_SETTING_VALUE)
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

        Registration registration = registrations.get(module.getModuleRegistrationId());

        if (registration == null) {
            throw new ModuleExistException("The module with the ID is not registered: {0}", module.getModuleRegistrationId());
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
