package nexo.coding.task.mfa.service.services;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import nexo.coding.task.mfa.service.infrastructure.exceptions.CodedException;
import nexo.coding.task.mfa.service.model.MfaCodeVerificationDto;
import nexo.coding.task.mfa.service.model.MfaToken;
import nexo.coding.task.mfa.service.repositories.MfaTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Sql(scripts = "/sql/mfaTestData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class MfaServiceTest {

    public static final String TEST_MAIL = "test@nexo.com";
    public static final String CODE = "123456";
    public static final LocalDateTime TOKEN_EXPIRATION_TIME = LocalDateTime.of(2024, 7, 3, 11, 58, 0);
    public static final String USER_EMAIL = "user2@example.com";
    public static final String NON_EXISTING_EMAIL = "nontexisting@example.com";
    public static final String EMAIL = "user1@example.com";
    public static final String NOT_EXPIRED_CODE = "968574";
    public static final String NEW_CODE = "999999";
    public static final String CODE1 = "654321";

    @Autowired
    private MfaService mfaService;
    @Autowired
    private MfaTokenRepository mfaTokenRepository;
    @MockBean
    private MailServerMock mailServerMock;
    @MockBean
    private MfaTokenGenerator mfaTokenGenerator;

    @Test
    @SneakyThrows
    @Transactional
    void shouldGenerateAndSendMfaCodeSuccessfully() {
        MfaToken token = MfaToken.create(CODE, TOKEN_EXPIRATION_TIME);

        when(mfaTokenGenerator.generateMfaTokenWithCode()).thenReturn(token);
        when(mailServerMock.sendEmailWithMfaCode()).thenReturn(true);

        mfaService.sendMfaCode(TEST_MAIL);

        Optional<MfaToken> savedToken = mfaTokenRepository.findByEmailAndCode(TEST_MAIL, CODE);
        assertThat(savedToken).isPresent();
        assertThat(savedToken.get().getCode()).isEqualTo(CODE);
        assertThat(savedToken.get().getEmail()).isEqualTo(TEST_MAIL);
        assertThat(savedToken.get().getTimeOfDelivery()).isNotNull();
        assertThat(savedToken.get().getExpirationTime()).isEqualTo(TOKEN_EXPIRATION_TIME);
    }

    @Test
    @SneakyThrows
    void shouldGenerateMfaCodeButNotSendItWhenMailServerFails() {
        MfaToken token = MfaToken.create(CODE, TOKEN_EXPIRATION_TIME);

        when(mfaTokenGenerator.generateMfaTokenWithCode()).thenReturn(token);
        when(mailServerMock.sendEmailWithMfaCode()).thenReturn(false);

        assertThrows(CodedException.class, () -> mfaService.sendMfaCode(TEST_MAIL));

        Optional<MfaToken> savedToken = mfaTokenRepository.findByEmailAndCode(token.getEmail(), token.getCode());
        assertThat(savedToken).isEmpty();
    }

    @Test
    @SneakyThrows
    @Transactional
    void shouldGenerateNewMfaCodeAndSendItWhenAlreadyGeneratedCodeIsExpired() {

        MfaToken newToken = MfaToken.create(NEW_CODE, TOKEN_EXPIRATION_TIME);
        when(mfaTokenGenerator.generateMfaTokenWithCode()).thenReturn(newToken);
        when(mailServerMock.sendEmailWithMfaCode()).thenReturn(true);

        mfaService.sendMfaCode(EMAIL);

        Optional<MfaToken> savedToken = mfaTokenRepository.findByEmailAndCode(EMAIL, NEW_CODE);
        assertThat(savedToken).isPresent();
        assertThat(savedToken.get().getCode()).isEqualTo(NEW_CODE);
        assertThat(savedToken.get().getEmail()).isEqualTo(EMAIL);
        assertThat(savedToken.get().getTimeOfDelivery()).isNotNull();
        assertThat(savedToken.get().getExpirationTime()).isEqualTo(TOKEN_EXPIRATION_TIME);
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionWhenValidMfaCodeAlreadyExists() {
        mfaTokenRepository.findByEmailAndTimeOfDeliveryIsNotNull(USER_EMAIL);

        assertThrows(CodedException.class, () -> mfaService.sendMfaCode(USER_EMAIL));
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionWhenMfaCodeNotFound() {
        MfaCodeVerificationDto verificationDto = new MfaCodeVerificationDto(NON_EXISTING_EMAIL, CODE);

        assertThrows(CodedException.class, () -> mfaService.verifyMfaCode(verificationDto));
    }

    @Test
    @SneakyThrows
    void shouldReturnTrueWhenMfaCodeIsNotExpired() {
        MfaCodeVerificationDto verificationDto = new MfaCodeVerificationDto(USER_EMAIL, NOT_EXPIRED_CODE);

        boolean isValid = mfaService.verifyMfaCode(verificationDto);

        assertThat(isValid).isTrue();
    }


    @Test
    @SneakyThrows
    void shouldReturnFalseWhenMfaCodeIsExpired() {

        MfaCodeVerificationDto verificationDto = new MfaCodeVerificationDto(EMAIL, CODE1);

        boolean isValid = mfaService.verifyMfaCode(verificationDto);

        assertThat(isValid).isFalse();
    }
}