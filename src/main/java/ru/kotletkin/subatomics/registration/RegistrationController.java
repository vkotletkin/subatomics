package ru.kotletkin.subatomics.registration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.kotletkin.subatomics.common.dto.BaseResponse;
import ru.kotletkin.subatomics.registration.dto.RegistrationDTO;
import ru.kotletkin.subatomics.registration.dto.RegistrationPage;
import ru.kotletkin.subatomics.registration.dto.RegistrationRequest;
import ru.kotletkin.subatomics.registration.dto.RegistrationSort;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

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
    public BaseResponse createRegister(@Valid @RequestBody RegistrationRequest registrationDTO) {
        return registrationService.registerMicroservice(registrationDTO);
    }
}
