package ru.kotletkin.aard.common.config;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    GitlabConf gitlab;

    @Getter
    @Setter
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GitlabConf {
        String url;
        String token;
        long projectId;
        String branch;
    }
}
