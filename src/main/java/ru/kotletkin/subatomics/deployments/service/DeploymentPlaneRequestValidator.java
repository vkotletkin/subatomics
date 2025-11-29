package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;
import ru.kotletkin.subatomics.common.exception.IncorrectModulesException;
import ru.kotletkin.subatomics.deployments.dto.backend.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.backend.DeployParametersDTO;
import ru.kotletkin.subatomics.deployments.dto.backend.DeployRequest;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeploymentPlaneRequestValidator {

    private final DeploymentsConfig deploymentsConfig;

    public void validate(DeployRequest request) {

        if (request.getModules() == null || request.getModules().isEmpty()) {
            throw new IncorrectModulesException("Список модулей не может быть пустым");
        }

        this.checkParameters(request);
    }

    public void validateModulesConsistency(Collection<DeployModuleDTO> requestedModules,
                                           Map<Long, Registration> registrations) {

        requestedModules.forEach(module -> validateModuleConsistency(module, registrations));
    }

    public void validateModuleConsistency(DeployModuleDTO module, Map<Long, Registration> registrations) {

        Long moduleId = module.getModuleRegistrationId();
        Registration registration = registrations.get(moduleId);

        if (registration == null) {
            throw new IncorrectModulesException("Модуль с идентификатором {0} не найден", moduleId);
        }

        if (!registration.getEnvironmentVariables().equals(module.getEnvironments())) {
            throw new IncorrectModulesException(
                    "Переменные окружения не совпадают с зарегистрированными для модуля с идентификатором: {0}", moduleId);
        }
    }

    private void checkParameters(DeployRequest request) {
        request.getModules().values().stream()
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
