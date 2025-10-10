package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabProjectServiceImpl {

    private final GitLabApi gitLabApi;

    public Long getProjectId(String projectPath) throws GitLabApiException {
        Project project = gitLabApi.getProjectApi().getProject(projectPath);
        log.info("Project ID for '{}': {}", projectPath, project.getId());
        return project.getId();
    }

    // Получить все проекты пользователя
    public List<Project> getUserProjects() throws GitLabApiException {
        return gitLabApi.getProjectApi().getProjects();
    }

    // Найти проект по имени
    public Project findProjectByName(String projectName) throws GitLabApiException {
        List<Project> projects = gitLabApi.getProjectApi().getProjects(projectName);
        if (!projects.isEmpty()) {
            return projects.getFirst();
        }
        throw new RuntimeException("Project not found: " + projectName);
    }

    // Альтернативный вариант с явными параметрами
    public void createFileExplicit(Long projectId, String filePath, String content,
                                   String branch, String commitMessage) throws GitLabApiException {

        RepositoryFile file = new RepositoryFile();
        file.setFilePath(filePath);
        file.setContent(content);
        file.setEncoding(Constants.Encoding.TEXT);


        gitLabApi.getRepositoryFileApi().createFile(projectId, file, branch, commitMessage);
//
//        CommitAction ca = new CommitAction().
//
//        gitLabApi.getCommitsApi().createCommit()

    }
}
