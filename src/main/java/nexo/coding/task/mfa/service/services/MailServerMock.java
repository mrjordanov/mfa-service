package nexo.coding.task.mfa.service.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailServerMock {

    private static final int MAX_RETRIES = 3;
    private final Random random = new Random();

    public boolean sendEmailWithMfaCode() {
        return sendEmailWithMfaCode(0);
    }

    private boolean sendEmailWithMfaCode(int attempt) {
        boolean isDeliveredSuccessful = random.nextInt(100) < 95;
        if (isDeliveredSuccessful) {
            return true;
        } else {
            if (attempt < MAX_RETRIES) {
                return sendEmailWithMfaCode(attempt + 1);
            } else {
                return false;
            }
        }
    }
}