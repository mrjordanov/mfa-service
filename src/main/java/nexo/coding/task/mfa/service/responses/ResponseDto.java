package nexo.coding.task.mfa.service.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResponseDto<T> {

    private final T response;
    private final Error error;

    public static <T> ResponseDto<T> respond(T content) {
        return new ResponseDto<>(content, null);
    }

    public static ResponseDto<Void> fail(String code, String message) {
        return new ResponseDto<>(null, new Error(code, message));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Error {
        private final String code;
        private final String message;
    }
}