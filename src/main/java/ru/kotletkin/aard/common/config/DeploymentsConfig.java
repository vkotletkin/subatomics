package ru.kotletkin.aard.common.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "deployments")
public class DeploymentsConfig {

    Parameters parameters;

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Parameters {
        Defaults defaults;
    }

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Defaults {
        Limits limits;
        Probes probes;
    }

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Limits {
        String cpu;
        String memory;
    }

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Probes {
        String liveness;
        String readiness;
    }
}
