package ru.kotletkin.subatomics.common.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class ModuleExistException extends RuntimeException {

    public ModuleExistException(String message) {
        super(message);
    }

    public ModuleExistException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<ModuleExistException> moduleExistException(String message, Object... args) {
        return () -> new ModuleExistException(message, args);
    }

    public static Supplier<ModuleExistException> moduleExistException(String message) {
        return () -> new ModuleExistException(message);
    }
}
