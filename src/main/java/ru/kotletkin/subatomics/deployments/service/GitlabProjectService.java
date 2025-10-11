package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CommitAction;
import org.gitlab4j.api.models.TreeItem;
import org.gitlab4j.models.Constants;
import org.springframework.stereotype.Service;
import ru.kotletkin.subatomics.common.dto.CatalogDTO;
import ru.kotletkin.subatomics.deployments.dto.DeploymentPlaneInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabProjectService {

    private final GitLabApi gitLabApi;

    public void createDeploy(String deployName, String author, Map<String, String> deployments) throws GitLabApiException {

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

        gitLabApi.getCommitsApi().createCommit(2L, "main", "Create by " + author + ". Project: "
                + deployName, null, "Deployer Service", author, commitActions);

    }

    public List<DeploymentPlaneInfo> findAllDeployments() {
        try {

            List<TreeItem> treeItems = gitLabApi.getRepositoryApi().getTree(2L, null, "main", true);

            List<String> modules = treeItems.stream()
                    .filter(item -> item.getType().equals(TreeItem.Type.TREE))
                    .map(TreeItem::getPath)
                    .toList();


            return modules.stream()
                    .map(deploymentPlane -> {
                        List<CatalogDTO> moduleDTOs = treeItems.stream()
                                .filter(item -> item.getPath().contains(deploymentPlane))
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
            throw new RuntimeException(e);
        }
    }

}
