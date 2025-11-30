package ru.kotletkin.aard.common.exception.dto;

import java.util.List;

public record ValidationErrorResponse(List<ErrorResponse> errors) {
}