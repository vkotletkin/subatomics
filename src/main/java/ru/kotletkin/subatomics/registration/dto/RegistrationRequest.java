package ru.kotletkin.subatomics.registration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.util.Map;

public record RegistrationRequest(

        // Base parameters
        @NotBlank String name,
        @NotBlank String version,
        @NotBlank String image,

        // Author Info
        @NotBlank String author,

        @URL @NotBlank String gitlabLink,

        // Environment variables
        @NotNull
        Map<String, String> environmentVariables
) {
}
