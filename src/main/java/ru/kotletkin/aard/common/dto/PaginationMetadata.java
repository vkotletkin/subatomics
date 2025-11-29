package ru.kotletkin.aard.common.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationMetadata {
    int offset;
    int limit;
    long count;
}
