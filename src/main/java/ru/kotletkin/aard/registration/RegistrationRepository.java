package ru.kotletkin.aard.registration;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kotletkin.aard.registration.model.Registration;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByIdIn(List<Long> ids);

    List<Registration> findByImage(String image);

    List<Registration> findByNameAndVersion(String name, String version);
}
