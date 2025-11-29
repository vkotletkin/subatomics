package ru.kotletkin.aard.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Aard Platform",
                version = "1.0",
                description = "API Documentation of Aard CaaS Platform"
        )
)
public class SwaggerConfig {
}