package nexo.coding.task.mfa.service.services;

import nexo.coding.task.mfa.service.infrastructure.exceptions.CodedException;
import nexo.coding.task.mfa.service.infrastructure.exceptions.ErrorCode;
import nexo.coding.task.mfa.service.model.MfaToken;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class MfaTokenGenerator {

    private static final int CODE_LENGTH = 6;
    public static final long TIME_STEP_MINUTES = 1;

    public MfaToken generateMfaTokenWithCode() throws CodedException {
        try {
            SecretKey secretKey = KeyGenerator.getInstance("HmacSHA1").generateKey();
            String code = generateMfaCode(secretKey);
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(TIME_STEP_MINUTES);

            return MfaToken.create(code, expirationTime);
        } catch (NoSuchAlgorithmException e) {
            throw new CodedException(ErrorCode.MFA_CODE_GENERATION_FAILED, e.getMessage());
        }
    }

    private String generateMfaCode(SecretKey secretKey) throws CodedException {
        long currentTimeStep = Instant.now().getEpochSecond() / TIME_STEP_MINUTES;
        byte[] timeStepBytes = ByteBuffer.allocate(8).putLong(currentTimeStep).array();
        byte[] hash = hmacSha1(secretKey.getEncoded(), timeStepBytes);
        int offset = hash[hash.length - 1] & 0xF;
        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
        int otp = binary % (int) Math.pow(10, CODE_LENGTH);

        return String.format("%06d", otp);
    }

    private byte[] hmacSha1(byte[] keyBytes, byte[] data) throws CodedException {
        try {
            Mac hmac = Mac.getInstance("HmacSHA1");
            SecretKeySpec key = new SecretKeySpec(keyBytes, "RAW");
            hmac.init(key);
            return hmac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new CodedException(ErrorCode.MFA_CODE_GENERATION_FAILED, e.getMessage());
        }
    }
}