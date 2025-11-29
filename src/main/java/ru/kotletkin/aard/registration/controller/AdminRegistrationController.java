package ru.kotletkin.aard.registration.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.aard.registration.RegistrationService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/registrations")
@Tag(name = "Admin Registrations API", description = "Administrative operations for registrations of services")
public class AdminRegistrationController {

    private final RegistrationService registrationService;

    @DeleteMapping("/{id}")
    public void deleteRegistration(@PathVariable long id, @RequestHeader(name = "X-Action-Username") String actionUsername) {
        log.info("Удаление регистрации с идентификатором: {} администратором: {}", id, actionUsername);
        registrationService.deleteById(id);
    }
}
