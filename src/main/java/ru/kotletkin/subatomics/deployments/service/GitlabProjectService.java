package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CommitAction;
import org.gitlab4j.api.models.TreeItem;
import org.gitlab4j.models.Constants;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.config.AppConfig;
import ru.kotletkin.subatomics.common.dto.CatalogDTO;
import ru.kotletkin.subatomics.common.exception.GitlabServiceException;
import ru.kotletkin.subatomics.deployments.dto.DeploymentPlaneInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabProjectService {

    private final GitLabApi gitLabApi;
    private final AppConfig appConfig;

    public void createDeploy(String deployName, String author, Map<String, String> deployments) {

        try {
            List<CommitAction> commitActions = new ArrayList<>();

            CommitAction dirAction = new CommitAction();
            dirAction.setAction(CommitAction.Action.CREATE);
            dirAction.setFilePath(deployName + "/.gitkeep");
            dirAction.setContent("");
            dirAction.setEncoding(Constants.Encoding.TEXT);

            commitActions.add(dirAction);

            for (Map.Entry<String, String> entry : deployments.entrySet()) {
                CommitAction entryDirAction = new CommitAction();
                entryDirAction.setAction(CommitAction.Action.CREATE);
                entryDirAction.setFilePath(deployName + "/" + entry.getKey() + ".yaml");
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

    public List<DeploymentPlaneInfo> findAllDeployments() {
        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(appConfig.getGitlab().getProjectId(), null,
                    appConfig.getGitlab().getBranch(), true);

            List<String> modules = treeItems.stream()
                    .filter(item -> item.getType().equals(TreeItem.Type.TREE))
                    .map(TreeItem::getPath)
                    .toList();


            return modules.stream()
                    .map(deploymentPlane -> {
                        List<CatalogDTO> moduleDTOs = treeItems.stream()
                                .filter(item -> item.getPath().startsWith(deploymentPlane + "/"))
                                .filter(item -> item.getType() == TreeItem.Type.BLOB)
                                .filter(item -> !item.getName().equals(".gitkeep"))
                                .map(item -> {
                                    String[] nameParts = item.getName().split("-");
                                    return CatalogDTO.builder()
                                            .id(Integer.parseInt(nameParts[1]))
                                            .name(nameParts[2])
                                            .build();
                                })
                                .toList();

                        return DeploymentPlaneInfo.builder()
                                .deploymentPlane(deploymentPlane)
                                .modules(moduleDTOs)
                                .build();
                    })
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
                    .filter(path -> path.startsWith(deploymentName + "/"))
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

}
