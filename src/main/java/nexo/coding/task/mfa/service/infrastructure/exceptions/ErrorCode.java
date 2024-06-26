package nexo.coding.task.mfa.service.infrastructure.exceptions;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public enum ErrorCode {

    EMAIL_NOT_FOUND("MFA001", "Email account not found."),
    MFA_CODE_GENERATION_FAILED("MFA002", "MFA code generation failed."),
    MFA_TOKEN_NOT_FOUND("MFA003", "MFA code not found."),
    VALID_MFA_CODE_ALREADY_EXISTS("MFA004", "A valid MFA code is already sent to {0}."),
    MFA_CODE_DELIVERY_FAILED("MFA005", "MFA code delivery to {0} failed. Please try again to generate."),
    VALIDATION_ERROR("MFA006", "Invalid fields: {0}.");

    private final String code;
    private final String pattern;

    ErrorCode(String code, String pattern) {
        this.code = code;
        this.pattern = pattern;
    }

    public String toMessage(Object... args) {

        return MessageFormat.format(pattern, args);
    }
}