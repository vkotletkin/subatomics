package ru.kotletkin.aard.common.config;

import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.GitLabApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GitLabConfig {

    private final AppConfig appConfig;

    @Bean
    public GitLabApi gitLabApi() {
        return new GitLabApi(appConfig.getGitlab().getUrl(), appConfig.getGitlab().getToken());
    }
}