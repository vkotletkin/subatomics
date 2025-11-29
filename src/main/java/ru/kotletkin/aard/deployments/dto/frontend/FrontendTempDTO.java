package ru.kotletkin.aard.deployments.dto.frontend;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FrontendTempDTO {
    String moduleId;
    String x;
    String y;
}
