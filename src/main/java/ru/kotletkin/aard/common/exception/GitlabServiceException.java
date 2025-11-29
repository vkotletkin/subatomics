package ru.kotletkin.aard.common.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class GitlabServiceException extends RuntimeException {

    public GitlabServiceException(String message) {
        super(message);
    }

    public GitlabServiceException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<GitlabServiceException> gitlabServiceException(String message, Object... args) {
        return () -> new GitlabServiceException(message, args);
    }

    public static Supplier<GitlabServiceException> gitlabServiceException(String message) {
        return () -> new GitlabServiceException(message);
    }
}
