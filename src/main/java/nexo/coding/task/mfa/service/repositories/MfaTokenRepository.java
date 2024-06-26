package nexo.coding.task.mfa.service.repositories;

import nexo.coding.task.mfa.service.model.MfaToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MfaTokenRepository extends JpaRepository<MfaToken, Long> {

    Optional<MfaToken> findByEmailAndCode(String email, String code);

    Optional<MfaToken> findByEmailAndTimeOfDeliveryIsNotNull(String email);
}