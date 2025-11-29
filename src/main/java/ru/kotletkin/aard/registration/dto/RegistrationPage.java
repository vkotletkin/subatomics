package ru.kotletkin.aard.registration.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.kotletkin.aard.common.dto.PaginationMetadata;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationPage {
    PaginationMetadata paginationMetadata;
    List<RegistrationDTO> registrations;
}
