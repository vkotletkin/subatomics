package ru.kotletkin.subatomics.registration;


import ru.kotletkin.subatomics.common.dto.BaseResponse;
import ru.kotletkin.subatomics.registration.dto.RegistrationDTO;
import ru.kotletkin.subatomics.registration.dto.RegistrationPage;
import ru.kotletkin.subatomics.registration.dto.RegistrationRequest;
import ru.kotletkin.subatomics.registration.dto.RegistrationSort;

public interface RegistrationService {

    RegistrationPage findAll(int offset, int limit, RegistrationSort sort);

    RegistrationDTO findById(long id);

    BaseResponse registerMicroservice(RegistrationRequest registrationDTO);
}
