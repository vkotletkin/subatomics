package ru.kotletkin.subatomics.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kotletkin.subatomics.registration.model.Registration;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByIdIn(List<Long> ids);
}
