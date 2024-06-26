package nexo.coding.task.mfa.service.infrastructure.exceptions;

import lombok.Getter;

@Getter
public class CodedException extends Exception {

    private final String code;

    public CodedException(ErrorCode code, Object... args) {
        super(code.toMessage(args));
        this.code = code.getCode();
    }
}