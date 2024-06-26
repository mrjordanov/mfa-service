package nexo.coding.task.mfa.service.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import nexo.coding.task.mfa.service.model.EmailDto;
import nexo.coding.task.mfa.service.model.MfaCodeVerificationDto;
import nexo.coding.task.mfa.service.services.MfaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import static nexo.coding.task.mfa.service.controllers.MfaController.VERIFY_CODE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static nexo.coding.task.mfa.service.controllers.MfaController.SEND_EMAIL;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("noauth")
class MfaControllerTest {

    private static final String JSON_RESPONSE = "$.response";
    public static final String JSON_ERROR = "$.error";
    public static final String TEST_MAIL = "test@nexo.com";
    public static final String TEST_CODE = "123456";
    public static final String VERIFIED_SUCCESSFULLY = "MFA code verified successfully";
    public static final String MFA_CODE_VERIFICATION_FAILED = "MFA code verification failed";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    MfaService mfaService;

    @Test
    @SneakyThrows
    void shouldSendMfaEmailSuccessfully() {
        String uri = buildUrl(SEND_EMAIL);
        String json = toJson(new EmailDto(TEST_MAIL));

        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_RESPONSE).isNotEmpty())
                .andExpect(jsonPath(JSON_ERROR).isEmpty());

        verify(mfaService).sendMfaCode(Mockito.eq(TEST_MAIL));
    }

    @Test
    @SneakyThrows
    void shouldVerifyMfaCodeSuccessfully() {
        String uri = buildUrl(VERIFY_CODE);
        String json = toJson(createMfaCodeVerificationDto());

        when(mfaService.verifyMfaCode(any(MfaCodeVerificationDto.class))).thenReturn(true);

        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_RESPONSE).value(VERIFIED_SUCCESSFULLY))
                .andExpect(jsonPath(JSON_ERROR).isEmpty());

        verify(mfaService).verifyMfaCode(any(MfaCodeVerificationDto.class));
    }

    @Test
    @SneakyThrows
    void shouldVerifyMfaCodeFail() {
        String uri = buildUrl(VERIFY_CODE);
        String json = toJson(createMfaCodeVerificationDto());

        when(mfaService.verifyMfaCode(any(MfaCodeVerificationDto.class))).thenReturn(false);

        mockMvc.perform(post(uri).contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_RESPONSE).value(MFA_CODE_VERIFICATION_FAILED))
                .andExpect(jsonPath(JSON_ERROR).isEmpty());

        verify(mfaService).verifyMfaCode(any(MfaCodeVerificationDto.class));
    }

    private MfaCodeVerificationDto createMfaCodeVerificationDto() {
        return new MfaCodeVerificationDto(TEST_MAIL, TEST_CODE);
    }

    private String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    private String buildUrl(String path) {
        return UriComponentsBuilder.fromPath(MfaController.BASE_URL).path(path).toUriString();
    }
}