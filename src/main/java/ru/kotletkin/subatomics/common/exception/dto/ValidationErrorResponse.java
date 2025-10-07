package ru.kotletkin.subatomics.common.exception.dto;

import java.util.List;

public record ValidationErrorResponse(List<ErrorResponse> validationErrors) {
}