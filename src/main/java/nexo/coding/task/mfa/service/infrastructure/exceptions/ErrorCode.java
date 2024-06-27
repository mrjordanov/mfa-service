package nexo.coding.task.mfa.service.infrastructure.exceptions;

import lombok.Getter;

import java.text.MessageFormat;

@Getter
public enum ErrorCode {

    MFA_CODE_GENERATION_FAILED("MFA001", "MFA code generation failed."),
    MFA_TOKEN_NOT_FOUND("MFA002", "MFA code not found."),
    VALID_MFA_CODE_ALREADY_EXISTS("MFA003", "A valid MFA code is already sent to {0}."),
    MFA_CODE_DELIVERY_FAILED("MFA004", "MFA code delivery to {0} failed. Please try again to generate."),
    VALIDATION_ERROR("MFA005", "Invalid fields: {0}.");

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