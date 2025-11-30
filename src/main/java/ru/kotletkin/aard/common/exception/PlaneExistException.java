package ru.kotletkin.aard.common.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class PlaneExistException extends RuntimeException {

    public PlaneExistException(String message) {
        super(message);
    }

    public PlaneExistException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<PlaneExistException> planeExistException(String message, Object... args) {
        return () -> new PlaneExistException(message, args);
    }

    public static Supplier<PlaneExistException> planeExistException(String message) {
        return () -> new PlaneExistException(message);
    }
}
