package ru.kotletkin.subatomics.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CaaS Backend by V.Kotletkin",
                version = "1.0",
                description = "API Documentation of CaaS Backend"
        )
)
public class SwaggerConfig {
}