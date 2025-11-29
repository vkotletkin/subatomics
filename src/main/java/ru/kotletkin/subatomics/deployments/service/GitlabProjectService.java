package ru.kotletkin.subatomics.deployments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CommitAction;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.gitlab4j.models.Constants;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.AppConfig;
import ru.kotletkin.subatomics.common.exception.GitlabServiceException;
import ru.kotletkin.subatomics.deployments.dto.DeploymentPlaneEnrichedDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabProjectService {

    private static final String SCHEMA_DIRECTORY_PATH = "core/schemas";
    private static final String APP_DIRECTORY_PATH = "apps/";

    private final GitLabApi gitLabApi;
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    public void createDeploy(String coreSchemaBody, String deployName, String author,
                             Map<String, String> deployments) {

        try {
            List<CommitAction> commitActions = new ArrayList<>();

            CommitAction dirAction = new CommitAction();
            dirAction.setAction(CommitAction.Action.CREATE);
            dirAction.setFilePath(APP_DIRECTORY_PATH + deployName + "/.gitkeep");
            dirAction.setContent("");
            dirAction.setEncoding(Constants.Encoding.TEXT);

            CommitAction schemaAction = new CommitAction();
            schemaAction.setAction(CommitAction.Action.CREATE);
            schemaAction.setFilePath(SCHEMA_DIRECTORY_PATH + "/" + deployName + ".json");
            schemaAction.setContent(coreSchemaBody);
            schemaAction.setEncoding(Constants.Encoding.TEXT);

            commitActions.add(dirAction);
            commitActions.add(schemaAction);

            for (Map.Entry<String, String> entry : deployments.entrySet()) {
                CommitAction entryDirAction = new CommitAction();
                entryDirAction.setAction(CommitAction.Action.CREATE);
                entryDirAction.setFilePath(APP_DIRECTORY_PATH + deployName + "/" + entry.getKey() + ".yaml");
                entryDirAction.setContent(entry.getValue());  // Пустой файл для создания директории
                entryDirAction.setEncoding(Constants.Encoding.TEXT);
                commitActions.add(entryDirAction);
            }

            gitLabApi.getCommitsApi().createCommit(appConfig.getGitlab().getProjectId(), appConfig.getGitlab().getBranch(),
                    "Created by " + author + ". Project: " + deployName, null, "deployer@zov.ru", author, commitActions);

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Ошибка при развертывании нового плана. {0}", e.getMessage());
        }
    }

    public List<DeploymentPlaneEnrichedDTO> findAllDeployments() {
        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(appConfig.getGitlab().getProjectId(), null,
                    appConfig.getGitlab().getBranch(), true);

            return treeItems.stream()
                    .filter(i -> i.getType().equals(TreeItem.Type.BLOB))
                    .filter(i -> i.getPath().contains(SCHEMA_DIRECTORY_PATH) && !i.getPath().contains(".gitkeep"))
                    .map(i -> getFileOnPath(i.getPath()))
                    .map(RepositoryFile::getDecodedContentAsString)
                    .map(this::castFromStringToEnriched)
                    .toList();

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Ошибка при поиске всех запущенных планов развертывания");
        }
    }

    public void deleteDeployment(String author, String deploymentName) {

        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(appConfig.getGitlab().getProjectId(), null,
                    appConfig.getGitlab().getBranch(), true);

            List<String> fileNames = treeItems.stream()
                    .filter(item -> item.getType().equals(TreeItem.Type.BLOB))
                    .map(TreeItem::getPath)
                    .filter(path -> path.startsWith(APP_DIRECTORY_PATH + deploymentName + "/"))
                    .toList();

            List<CommitAction> commitActions = fileNames.stream()
                    .map(i -> new CommitAction().withAction(CommitAction.Action.DELETE).withFilePath(i))
                    .toList();

            gitLabApi.getCommitsApi().createCommit(appConfig.getGitlab().getProjectId(), appConfig.getGitlab().getBranch(),
                    "Deleted by " + author + ". Project: " + deploymentName, null, "deployer@zov.ru", author, commitActions);

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Ошибка при удалении плана развертывания");
        }

    }

    private RepositoryFile getFileOnPath(String path) {
        try {
            return gitLabApi.getRepositoryFileApi().getFile(appConfig.getGitlab().getProjectId(), path, appConfig.getGitlab().getBranch());
        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Ошибка при получении исходной схемы из репозитория. Путь к схеме: {0}", path);
        }
    }

    private DeploymentPlaneEnrichedDTO castFromStringToEnriched(String jsonBody) {
        try {
            return objectMapper.readValue(jsonBody, DeploymentPlaneEnrichedDTO.class);
        } catch (JsonProcessingException e) {
            throw new GitlabServiceException("Проблема при десериализации схемы деплоя");
        }
    }

}
