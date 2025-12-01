package ru.kotletkin.aard;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@Theme(variant = Lumo.LIGHT)
public class AardPlatformApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(AardPlatformApplication.class, args);
    }
}
