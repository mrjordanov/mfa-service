package nexo.coding.task.mfa.service.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexo.coding.task.mfa.service.infrastructure.exceptions.CodedException;
import nexo.coding.task.mfa.service.model.EmailDto;
import nexo.coding.task.mfa.service.model.MfaCodeVerificationDto;
import nexo.coding.task.mfa.service.responses.ResponseDto;
import nexo.coding.task.mfa.service.services.MfaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static nexo.coding.task.mfa.service.responses.ResponseDto.respond;

@RestController
@RequestMapping(MfaController.BASE_URL)
@RequiredArgsConstructor
@Validated
@Slf4j
public class MfaController {

    public static final String BASE_URL = "/api/v1/mfa";
    public static final String SEND_EMAIL = "/send-email";
    public static final String VERIFY_CODE = "/verify-code";

    private final MfaService mfaService;

    @PostMapping(SEND_EMAIL)
    public ResponseDto<String> sendMfaEmail(@Valid @RequestBody EmailDto email) throws CodedException {
        mfaService.sendMfaCode(email.getAddress());
        return respond("MFA code generated and sent to " + email.getAddress());
    }

    @PostMapping(VERIFY_CODE)
    public ResponseDto<String> verifyMfaCode(@Valid @RequestBody MfaCodeVerificationDto mfaCodeVerificationDto) throws CodedException {
        log.info("Verifying MFA code for email: {}", mfaCodeVerificationDto.getEmail());
        boolean isValid = mfaService.verifyMfaCode(mfaCodeVerificationDto);
        return ResponseDto.respond(isValid ? "MFA code verified successfully" : "MFA code verification failed");
    }
}