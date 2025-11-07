package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.TokenRenovacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRenovacaoRepository extends JpaRepository<TokenRenovacao, UUID> {

    Optional<TokenRenovacao> findByToken(String token);

    List<TokenRenovacao> findByUsuarioIdAndRevogadoFalse(UUID usuarioId);

    @Modifying
    @Query("UPDATE TokenRenovacao rt SET rt.revogado = true WHERE rt.usuario.id = :usuarioId")
    void revogarTodosDoUsuario(@Param("usuarioId") UUID usuarioId);

    @Modifying
    @Query("DELETE FROM TokenRenovacao rt WHERE rt.expiraEm < :agora OR rt.revogado = true")
    void limparExpiradosERevogados(@Param("agora") OffsetDateTime agora);
}
