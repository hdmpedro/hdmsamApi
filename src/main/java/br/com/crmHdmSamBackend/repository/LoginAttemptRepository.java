package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.LoginAttempt;
import br.com.crmHdmSamBackend.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    long countByLoginAndTentativaEmAfterAndSucesso(
            String login,
            OffsetDateTime after,
            boolean sucesso
    );

    long countByIpAddressAndTentativaEmAfterAndSucesso(
            String ipAddress,
            OffsetDateTime after,
            boolean sucesso
    );

    @Modifying
    void deleteByLoginAndTentativaEmAfter(String login, OffsetDateTime after);

    @Modifying
    void deleteByIpAddressAndTentativaEmAfter(String ipAddress, OffsetDateTime after);

    @Modifying
    void deleteByTentativaEmBefore(OffsetDateTime before);
}