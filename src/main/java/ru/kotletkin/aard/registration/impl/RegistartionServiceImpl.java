package ru.kotletkin.aard.registration.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kotletkin.aard.common.dto.PaginationMetadata;
import ru.kotletkin.aard.common.exception.ModuleExistException;
import ru.kotletkin.aard.registration.RegistrationMapper;
import ru.kotletkin.aard.registration.RegistrationRepository;
import ru.kotletkin.aard.registration.RegistrationService;
import ru.kotletkin.aard.registration.dto.RegistrationDTO;
import ru.kotletkin.aard.registration.dto.RegistrationPage;
import ru.kotletkin.aard.registration.dto.RegistrationRequest;
import ru.kotletkin.aard.registration.dto.RegistrationSort;
import ru.kotletkin.aard.registration.model.Registration;

import static ru.kotletkin.aard.common.exception.NotFoundException.notFoundException;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistartionServiceImpl implements RegistrationService {

    private static final String NOT_FOUND_MESSAGE = "Регистрация модуля с идентификатором: {0} - не найдена";

    private final RegistrationMapper registrationMapper;
    private final RegistrationRepository registrationRepository;

    @Override
    public RegistrationPage findAll(int offset, int limit, RegistrationSort sort) {

        PageRequest pageRequest = PageRequest.of(offset, limit, sort.getSortValue());

        final Page<Registration> registrations = registrationRepository.findAll(pageRequest);

        PaginationMetadata paginationMetadata = PaginationMetadata.builder()
                .offset(offset)
                .limit(limit)
                .count(registrations.getTotalElements())
                .build();

        return RegistrationPage.builder()
                .paginationMetadata(paginationMetadata)
                .registrations(registrationMapper.toDTO(registrations.getContent()))
                .build();
    }

    @Override
    public RegistrationDTO findById(long id) {

        Registration registration = registrationRepository.findById(id).orElseThrow(notFoundException(NOT_FOUND_MESSAGE, id));

        return registrationMapper.toDTO(registration);
    }

    @Override
    @Transactional
    public RegistrationDTO registerMicroservice(RegistrationRequest registrationDTO) {

        if (!registrationRepository.findByImage(registrationDTO.image()).isEmpty() ||
                !registrationRepository.findByNameAndVersion(registrationDTO.name(), registrationDTO.version()).isEmpty()) {
            throw new ModuleExistException("Модуль с таким именем и версией, либо названием образа уже существует");
        }

        Registration registration = registrationMapper.toModel(registrationDTO);
        registrationRepository.save(registration);
        return registrationMapper.toDTO(registration);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        checkExistsById(id);
        registrationRepository.deleteById(id);
    }

    private void checkExistsById(long id) {
        registrationRepository.findById(id).orElseThrow(notFoundException(NOT_FOUND_MESSAGE, id));
    }
}
