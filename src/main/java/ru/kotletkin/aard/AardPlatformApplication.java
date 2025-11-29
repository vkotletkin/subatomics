package ru.kotletkin.aard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AardPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AardPlatformApplication.class, args);
    }

}
