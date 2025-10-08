package ru.kotletkin.subatomics.registration;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kotletkin.subatomics.common.dto.BaseResponse;
import ru.kotletkin.subatomics.common.dto.PaginationMetadata;
import ru.kotletkin.subatomics.registration.dto.RegistrationDTO;
import ru.kotletkin.subatomics.registration.dto.RegistrationPage;
import ru.kotletkin.subatomics.registration.dto.RegistrationRequest;
import ru.kotletkin.subatomics.registration.dto.RegistrationSort;

import static ru.kotletkin.subatomics.common.exception.NotFoundException.notFoundException;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegistartionServiceImpl implements RegistrationService {

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
        Registration registration = registrationRepository.findById(id).orElseThrow(
                notFoundException("Пользователь с идентификатором: {0} - не найден", id));

        return registrationMapper.toDTO(registration);
    }

    @Override
    @Transactional
    public BaseResponse registerMicroservice(RegistrationRequest registrationDTO) {
        Registration registration = registrationMapper.toModel(registrationDTO);
        registrationRepository.save(registration);
        return new BaseResponse("Заявка на регистрацию успешно отправлена", "Описание отсутствует");
    }
}
