package ru.kotletkin.subatomics.deployments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CommitAction;
import org.gitlab4j.models.Constants;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitlabProjectServiceImpl {

    private final GitLabApi gitLabApi;

    public void createDeploy(String deployName, String author, Map<String, String> deployments) throws GitLabApiException {

        List<CommitAction> commitActions = new ArrayList<>();

        CommitAction dirAction = new CommitAction();
        dirAction.setAction(CommitAction.Action.CREATE);
        dirAction.setFilePath(deployName + "/.gitkeep");
        dirAction.setContent("");  // Пустой файл для создания директории
        dirAction.setEncoding(Constants.Encoding.TEXT);

        commitActions.add(dirAction);

        for (Map.Entry<String, String> entry : deployments.entrySet()) {
            CommitAction entryDirAction = new CommitAction();
            entryDirAction.setAction(CommitAction.Action.CREATE);
            entryDirAction.setFilePath(deployName + "/" + entry.getKey());
            entryDirAction.setContent(entry.getValue());  // Пустой файл для создания директории
            entryDirAction.setEncoding(Constants.Encoding.TEXT);
            commitActions.add(entryDirAction);
        }

        gitLabApi.getCommitsApi().createCommit(2L, "main", "Create by " + author, null,
                "Deployer Service", author, commitActions);

    }

}
