package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;
import ru.kotletkin.subatomics.common.exception.IncorrectModulesException;
import ru.kotletkin.subatomics.deployments.dto.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployParametersDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeploymentPlaneRequestValidator {

    private final DeploymentsConfig deploymentsConfig;

    public void validate(DeployRequest request) {

        if (request.getModules() == null || request.getModules().isEmpty()) {
            throw new IllegalArgumentException("Список модулей не может быть пустым");
        }

        this.checkParameters(request);
    }

    public void validateSize(Map<Long, Registration> registrations, List<Long> moduleNames) {

        if (registrations.size() != moduleNames.size()) {
            throw new IncorrectModulesException("Modules names and registrations do not match");
        }
    }

    public void validateModulesConsistency(List<DeployModuleDTO> requestedModules,
                                           Map<Long, Registration> registrations) {

        requestedModules.forEach(module -> validateModuleConsistency(module, registrations));
    }

    public void validateModuleConsistency(DeployModuleDTO module, Map<Long, Registration> registrations) {
        Long moduleId = module.getId();
        Registration registration = registrations.get(moduleId);

        if (registration == null) {
            throw new IncorrectModulesException("Module {0} not found", moduleId);
        }

        if (!registration.getEnvironmentVariables().equals(module.getEnvironments())) {
            throw new IncorrectModulesException(
                    "Environment variables do not match for module {0}", moduleId);
        }
    }

    private void checkParameters(DeployRequest request) {
        request.getModules().stream()
                .filter(module -> module.getParameters() == null)
                .forEach(module -> module.setParameters(createDefaultParameters()));
    }

    private DeployParametersDTO createDefaultParameters() {
        return DeployParametersDTO.builder()
                .cpuLimit(deploymentsConfig.getParameters().getDefaults().getLimits().getCpu())
                .memoryLimit(deploymentsConfig.getParameters().getDefaults().getLimits().getMemory())
                .livenessProbe(deploymentsConfig.getParameters().getDefaults().getProbes().getLiveness())
                .readinessProbe(deploymentsConfig.getParameters().getDefaults().getProbes().getReadiness())
                .build();

    }
}
