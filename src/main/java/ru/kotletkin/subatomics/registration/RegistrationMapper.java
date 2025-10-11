package ru.kotletkin.subatomics.registration;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.kotletkin.subatomics.registration.dto.RegistrationDTO;
import ru.kotletkin.subatomics.registration.dto.RegistrationRequest;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface RegistrationMapper {

    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    Registration toModel(RegistrationRequest registrationDTO);

    RegistrationDTO toDTO(Registration registration);

    List<RegistrationDTO> toDTO(List<Registration> registrations);
}
