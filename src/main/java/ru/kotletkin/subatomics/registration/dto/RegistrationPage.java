package ru.kotletkin.subatomics.registration.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.kotletkin.subatomics.common.dto.PaginationMetadata;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationPage {
    PaginationMetadata paginationMetadata;
    List<RegistrationDTO> registrations;
}
