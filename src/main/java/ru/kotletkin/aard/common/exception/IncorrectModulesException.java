package ru.kotletkin.aard.common.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class IncorrectModulesException extends RuntimeException {

    public IncorrectModulesException(String message) {
        super(message);
    }

    public IncorrectModulesException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<IncorrectModulesException> incorrectModulesException(String message, Object... args) {
        return () -> new IncorrectModulesException(message, args);
    }

    public static Supplier<IncorrectModulesException> incorrectModulesException(String message) {
        return () -> new IncorrectModulesException(message);
    }
}
