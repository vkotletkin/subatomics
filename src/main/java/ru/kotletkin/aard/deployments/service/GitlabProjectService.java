package ru.kotletkin.aard.deployments.service;

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
import ru.kotletkin.aard.common.config.AppConfig;
import ru.kotletkin.aard.common.exception.GitlabServiceException;
import ru.kotletkin.aard.deployments.dto.DeploymentPlaneEnrichedDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.kotletkin.aard.common.exception.NotFoundException.notFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabProjectService {

    private static final String SCHEMA_DIRECTORY_PATH = "core/schemas";
    private static final String APP_DIRECTORY_PATH = "apps/";
    private static final String PATH_DELIMITER = "/";
    private static final String GITKEEP_FILENAME = ".gitkeep";

    private final GitLabApi gitLabApi;
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    public void createDeploy(String coreSchemaBody, String deployName, String author,
                             Map<String, String> deployments) {

        try {
            List<CommitAction> commitActions = new ArrayList<>();

            CommitAction dirAction = new CommitAction();
            dirAction.setAction(CommitAction.Action.CREATE);
            dirAction.setFilePath(APP_DIRECTORY_PATH + deployName + PATH_DELIMITER + GITKEEP_FILENAME);
            dirAction.setContent("");
            dirAction.setEncoding(Constants.Encoding.TEXT);

            CommitAction schemaAction = new CommitAction();
            schemaAction.setAction(CommitAction.Action.CREATE);
            schemaAction.setFilePath(SCHEMA_DIRECTORY_PATH + PATH_DELIMITER + deployName + ".json");
            schemaAction.setContent(coreSchemaBody);
            schemaAction.setEncoding(Constants.Encoding.TEXT);

            commitActions.add(dirAction);
            commitActions.add(schemaAction);

            for (Map.Entry<String, String> entry : deployments.entrySet()) {
                CommitAction entryDirAction = new CommitAction();
                entryDirAction.setAction(CommitAction.Action.CREATE);
                entryDirAction.setFilePath(APP_DIRECTORY_PATH + deployName + PATH_DELIMITER + entry.getKey() + ".yaml");
                entryDirAction.setContent(entry.getValue());  // Пустой файл для создания директории
                entryDirAction.setEncoding(Constants.Encoding.TEXT);
                commitActions.add(entryDirAction);
            }

            gitLabApi.getCommitsApi().createCommit(appConfig.getGitlab().getProjectId(), appConfig.getGitlab().getBranch(),
                    "Created by " + author + ". Project: " + deployName, null, "deployer@zov.ru", author, commitActions);

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("An error occurred when deploying a new plan. {0}", e.getMessage());
        }
    }

    public DeploymentPlaneEnrichedDTO updatePlane() {
        return null;
    }

    public DeploymentPlaneEnrichedDTO findDeploymentBodyOnName(String planeName) {

        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(appConfig.getGitlab().getProjectId(), null,
                    appConfig.getGitlab().getBranch(), true);

            return treeItems.stream()
                    .filter(i -> i.getType().equals(TreeItem.Type.BLOB))
                    .filter(i -> i.getPath().contains(SCHEMA_DIRECTORY_PATH) && !i.getPath().contains(GITKEEP_FILENAME))
                    .filter(i -> i.getPath().contains(planeName))
                    .map(i -> getFileOnPath(i.getPath()))
                    .map(RepositoryFile::getDecodedContentAsString)
                    .map(this::castFromStringToEnriched)
                    .findFirst()
                    .orElseThrow(notFoundException("Plane id with name: {0} - not found"));

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Error when searching for all running deployment plans");
        }
    }

    public List<String> findAllDeployments() {
        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(appConfig.getGitlab().getProjectId(), null,
                    appConfig.getGitlab().getBranch(), true);

            return treeItems.stream()
                    .filter(i -> i.getType().equals(TreeItem.Type.BLOB))
                    .filter(i -> i.getPath().contains(SCHEMA_DIRECTORY_PATH) && !i.getPath().contains(GITKEEP_FILENAME))
                    .map(i -> i.getName().replace(".json", ""))
                    .toList();

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Error when searching for all running deployment plans");
        }
    }

    public void deleteDeployment(String author, String deploymentName) {

        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(appConfig.getGitlab().getProjectId(), null,
                    appConfig.getGitlab().getBranch(), true);

            List<String> fileNames = treeItems.stream()
                    .filter(item -> item.getType().equals(TreeItem.Type.BLOB))
                    .map(TreeItem::getPath)
                    .filter(path -> path.startsWith(APP_DIRECTORY_PATH + deploymentName + PATH_DELIMITER))
                    .collect(Collectors.toList());

            List<String> coreItems = treeItems.stream()
                    .filter(item -> item.getType().equals(TreeItem.Type.BLOB))
                    .filter(item -> item.getPath().contains(SCHEMA_DIRECTORY_PATH) && !item.getPath().contains(GITKEEP_FILENAME))
                    .filter(item -> item.getName().contains(deploymentName))
                    .map(TreeItem::getPath)
                    .toList();

            fileNames.addAll(coreItems);


            List<CommitAction> commitActions = fileNames.stream()
                    .map(i -> new CommitAction().withAction(CommitAction.Action.DELETE).withFilePath(i))
                    .toList();

            gitLabApi.getCommitsApi().createCommit(appConfig.getGitlab().getProjectId(), appConfig.getGitlab().getBranch(),
                    "Deleted by " + author + ". Project: " + deploymentName, null, "deployer@zov.ru", author, commitActions);

        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Error deleting the deployment plan");
        }

    }

    private RepositoryFile getFileOnPath(String path) {
        try {
            return gitLabApi.getRepositoryFileApi().getFile(appConfig.getGitlab().getProjectId(), path, appConfig.getGitlab().getBranch());
        } catch (GitLabApiException e) {
            throw new GitlabServiceException("Error when getting the source schema from the repository. Path to the schema: {0}", path);
        }
    }

    private DeploymentPlaneEnrichedDTO castFromStringToEnriched(String jsonBody) {
        try {
            return objectMapper.readValue(jsonBody, DeploymentPlaneEnrichedDTO.class);
        } catch (JsonProcessingException e) {
            throw new GitlabServiceException("The problem with deserializing the deployment plane");
        }
    }
}
