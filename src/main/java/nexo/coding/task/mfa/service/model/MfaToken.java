package nexo.coding.task.mfa.service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static nexo.coding.task.mfa.service.services.MfaTokenGenerator.TIME_STEP_MINUTES;

@Entity(name = "mfa_token")
@Getter
@Setter
public class MfaToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "time_of_generation", nullable = false)
    private LocalDateTime timeOfGeneration;

    @Column(name = "time_of_delivery")
    private LocalDateTime timeOfDelivery;

    public static MfaToken create(String code, LocalDateTime expirationTime) {
        MfaToken mfaToken = new MfaToken();
        mfaToken.setCode(code);
        mfaToken.setExpirationTime(expirationTime);
        mfaToken.setTimeOfGeneration(expirationTime.minusMinutes(TIME_STEP_MINUTES));
        return mfaToken;
    }
}