package nexo.coding.task.mfa.service.services;

import lombok.SneakyThrows;
import nexo.coding.task.mfa.service.model.MfaToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


class MfaTokenGeneratorTest {

    @InjectMocks
    private MfaTokenGenerator mfaTokenGenerator;

    @Mock
    private KeyGenerator keyGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @SneakyThrows
    void shouldGenerateMfaTokenWithCode() {
        SecretKey secretKey = KeyGenerator.getInstance("HmacSHA1").generateKey();
        when(keyGenerator.generateKey()).thenReturn(secretKey);

        MfaToken mfaToken = mfaTokenGenerator.generateMfaTokenWithCode();

        LocalDateTime expectedExpirationTime = LocalDateTime.now().plusMinutes(MfaTokenGenerator.TIME_STEP_MINUTES);

        assertEquals(6, mfaToken.getCode().length());
        assertEquals(expectedExpirationTime.getYear(), mfaToken.getExpirationTime().getYear());
        assertEquals(expectedExpirationTime.getMonth(), mfaToken.getExpirationTime().getMonth());
        assertEquals(expectedExpirationTime.getDayOfMonth(), mfaToken.getExpirationTime().getDayOfMonth());
        assertEquals(expectedExpirationTime.getHour(), mfaToken.getExpirationTime().getHour());
        assertEquals(expectedExpirationTime.getMinute(), mfaToken.getExpirationTime().getMinute());
        assertEquals(expectedExpirationTime.getSecond(), mfaToken.getExpirationTime().getSecond());
    }
}