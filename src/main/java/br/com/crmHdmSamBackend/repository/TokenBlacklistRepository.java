package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {

    boolean existsByTokenJti(String tokenJti);

    @Modifying
    @Query("DELETE FROM TokenBlacklist tb WHERE tb.expiraEm < :agora")
    void limparExpirados(@Param("agora") OffsetDateTime agora);
}
