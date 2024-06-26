package nexo.coding.task.mfa.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MfaCodeVerificationDto {

    @NotEmpty(message = "Email address is required")
    @Email(message = "Email address should be valid")
    private String email;

    @NotEmpty(message = "MFA code should be provided")
    @Size(min = 6, max = 6, message = "MFA code should be exactly 6 digits long")
    @Pattern(regexp = "\\d{6}", message = "MFA code should be a 6-digit number")
    private String code;
}