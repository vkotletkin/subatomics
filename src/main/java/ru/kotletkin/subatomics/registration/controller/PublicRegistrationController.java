package ru.kotletkin.subatomics.registration.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.subatomics.registration.RegistrationService;
import ru.kotletkin.subatomics.registration.dto.RegistrationDTO;
import ru.kotletkin.subatomics.registration.dto.RegistrationPage;
import ru.kotletkin.subatomics.registration.dto.RegistrationRequest;
import ru.kotletkin.subatomics.registration.dto.RegistrationSort;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registrations")
@Tag(name = "Public Registrations API", description = "Public operations for registrations of services")
public class PublicRegistrationController {

    private final RegistrationService registrationService;

    @GetMapping
    public RegistrationPage getAllRegistrations(@RequestParam(name = "offset", defaultValue = "0") @Min(0) Integer offset,
                                                @RequestParam(name = "limit", defaultValue = "50") @Max(200) Integer limit,
                                                @RequestParam(name = "sort") RegistrationSort sort) {
        return registrationService.findAll(offset, limit, sort);
    }

    @GetMapping("/{id}")
    public RegistrationDTO getRegistration(@PathVariable long id) {
        return registrationService.findById(id);
    }

    @PostMapping
    public RegistrationDTO createRegister(@Valid @RequestBody RegistrationRequest registrationDTO, @RequestHeader(name = "X-Action-Username") String actionUsername) {
        log.info("Регистрация модуля из репозитория {} пользователем с именем: {}", registrationDTO.gitlabLink(), actionUsername);
        return registrationService.registerMicroservice(registrationDTO);
    }
}
