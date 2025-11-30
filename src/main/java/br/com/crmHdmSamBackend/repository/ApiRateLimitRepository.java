package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.ApiRateLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiRateLimitRepository extends JpaRepository<ApiRateLimit, UUID> {

    Optional<ApiRateLimit> findByIdentificadorAndEndpoint(String identificador, String endpoint);

    @Modifying
    @Query("DELETE FROM ApiRateLimit a WHERE a.janelaInicio < :limite")
    void limparJanelasAntigas(@Param("limite") OffsetDateTime limite);
}