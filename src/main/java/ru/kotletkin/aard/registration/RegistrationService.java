package ru.kotletkin.aard.registration;


import ru.kotletkin.aard.registration.dto.RegistrationDTO;
import ru.kotletkin.aard.registration.dto.RegistrationPage;
import ru.kotletkin.aard.registration.dto.RegistrationRequest;
import ru.kotletkin.aard.registration.dto.RegistrationSort;

public interface RegistrationService {

    RegistrationPage findAll(int offset, int limit, RegistrationSort sort);

    RegistrationDTO findById(long id);

    RegistrationDTO registerMicroservice(RegistrationRequest registrationDTO);

    void deleteById(long id);
}
