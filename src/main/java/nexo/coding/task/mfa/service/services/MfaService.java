package nexo.coding.task.mfa.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nexo.coding.task.mfa.service.infrastructure.exceptions.CodedException;
import nexo.coding.task.mfa.service.infrastructure.exceptions.ErrorCode;
import nexo.coding.task.mfa.service.model.MfaCodeVerificationDto;
import nexo.coding.task.mfa.service.model.MfaToken;
import nexo.coding.task.mfa.service.repositories.MfaTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MfaService {

    private final MfaTokenRepository mfaTokenRepository;
    private final MfaTokenGenerator mfaTokenGenerator;
    private final MailService mailService;

    @Transactional
    public void sendMfaCode(String email) throws CodedException {
        Optional<MfaToken> existingToken = mfaTokenRepository.findByEmailAndTimeOfDeliveryIsNotNull(email);
        if (existingToken.isPresent()) {
            handleExistingToken(existingToken.get());
        } else {
            generateAndSendNewMfaCode(email);
        }
    }

    private void handleExistingToken(MfaToken token) throws CodedException {
        if (token.getExpirationTime().isAfter(LocalDateTime.now())) {
            throw new CodedException(ErrorCode.VALID_MFA_CODE_ALREADY_EXISTS, token.getEmail());
        }

        mfaTokenRepository.delete(token);
        generateAndSendNewMfaCode(token.getEmail());
    }

    private void generateAndSendNewMfaCode(String email) throws CodedException {
        MfaToken token = mfaTokenGenerator.generateMfaTokenWithCode();
        log.info("Generated MFA token");
        token.setEmail(email);
        boolean isSuccessful = mailService.sendEmailWithMfaCode();
        if (!isSuccessful) {
            throw new CodedException(ErrorCode.MFA_CODE_DELIVERY_FAILED, email);
        }
        token.setTimeOfDelivery(LocalDateTime.now());
        log.info("MFA token sent to email: {} successfully", email);

        mfaTokenRepository.save(token);
    }

    public boolean verifyMfaCode(MfaCodeVerificationDto mfaCodeVerificationDto) throws CodedException {
        MfaToken token = mfaTokenRepository.findByEmailAndCode(mfaCodeVerificationDto.getEmail(), mfaCodeVerificationDto.getCode())
                .orElseThrow(() -> new CodedException(ErrorCode.MFA_TOKEN_NOT_FOUND, mfaCodeVerificationDto.getCode()));

        return isTokenValid(token);
    }

    private boolean isTokenValid(MfaToken token) {
        return token.getExpirationTime().isAfter(LocalDateTime.now()) && token.getTimeOfDelivery() != null;
    }
}