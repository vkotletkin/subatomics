package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.DeploymentsConfig;
import ru.kotletkin.subatomics.deployments.dto.DeployModuleDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployParametersDTO;
import ru.kotletkin.subatomics.deployments.dto.DeployRequest;
import ru.kotletkin.subatomics.registration.Registration;
import ru.kotletkin.subatomics.registration.RegistrationRepository;

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

    public void handleRequest(DeployRequest deployRequest) {

        validateRequest(deployRequest);

        List<DeployModuleDTO> modulesInRequest = deployRequest.getModules();

        List<String> modulesNames = modulesInRequest.stream().map(DeployModuleDTO::getName).toList();
        Map<String, Registration> registrations = registrationRepository.findByNameIn(modulesNames).stream()
                .collect(Collectors.toMap(Registration::getName, Function.identity()));

        // todo: add exception processing
        if (modulesNames.size() != registrations.size()) {
            throw new RuntimeException("Modules names and registrations do not match");
        }

        for (DeployModuleDTO module : modulesInRequest) {

            Registration registration = registrations.get(module.getName());

            if (registration == null) {
                throw new RuntimeException("Module " + module.getName() + " not found");
            }

            if (!registration.getEnvironmentVariables().equals(module.getEnvironments())) {
                throw new RuntimeException("Environment variables do not match");
            }

        }
        log.info(deployRequest.toString());



    }

    private void validateRequest(DeployRequest request) {

        if (request.getModules() == null || request.getModules().isEmpty()) {
            throw new IllegalArgumentException("Modules list cannot be empty");
        }

        request.getModules().forEach(module -> {
            if (StringUtils.isBlank(module.getName())) {
                throw new IllegalArgumentException("Module name cannot be blank");
            }

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
}
