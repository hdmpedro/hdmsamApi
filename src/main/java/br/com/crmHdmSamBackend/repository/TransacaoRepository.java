package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.Transacao;
import br.com.crmHdmSamBackend.model.enums.StatusTransacao;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    List<Transacao> findByUsuarioId(UUID usuarioId);

    List<Transacao> findByUsuarioIdOrderByDataDesc(UUID usuarioId);

    List<Transacao> findByUsuarioIdAndTipo(UUID usuarioId, TipoTransacao tipo);

    List<Transacao> findByUsuarioIdAndStatus(UUID usuarioId, StatusTransacao status);

    List<Transacao> findByUsuarioIdAndCategoria(UUID usuarioId, String categoria);

    @Query("SELECT t FROM Transacao t WHERE t.usuario.id = :usuarioId " +
            "AND t.data BETWEEN :dataInicio AND :dataFim " +
            "ORDER BY t.data DESC")
    List<Transacao> findByUsuarioIdAndDataBetween(
            @Param("usuarioId") UUID usuarioId,
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim
    );

    @Query("SELECT SUM(t.quantia) FROM Transacao t WHERE t.usuario.id = :usuarioId " +
            "AND t.tipo = :tipo AND t.status = 'CONFIRMADA'")
    BigDecimal sumByUsuarioIdAndTipo(
            @Param("usuarioId") UUID usuarioId,
            @Param("tipo") TipoTransacao tipo
    );

    @Query("SELECT SUM(t.quantia) FROM Transacao t WHERE t.usuario.id = :usuarioId " +
            "AND t.tipo = :tipo AND t.status = 'CONFIRMADA' " +
            "AND t.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumByUsuarioIdAndTipoAndDataBetween(
            @Param("usuarioId") UUID usuarioId,
            @Param("tipo") TipoTransacao tipo,
            @Param("dataInicio") OffsetDateTime dataInicio,
            @Param("dataFim") OffsetDateTime dataFim
    );

    @Query("SELECT t.categoria, SUM(t.quantia) FROM Transacao t " +
            "WHERE t.usuario.id = :usuarioId AND t.tipo = :tipo " +
            "AND t.status = 'CONFIRMADA' " +
            "GROUP BY t.categoria ORDER BY SUM(t.quantia) DESC")
    List<Object[]> sumByCategoria(
            @Param("usuarioId") UUID usuarioId,
            @Param("tipo") TipoTransacao tipo
    );

    @Query("SELECT COUNT(t) FROM Transacao t WHERE t.usuario.id = :usuarioId " +
            "AND t.status = :status")
    Long countByUsuarioIdAndStatus(
            @Param("usuarioId") UUID usuarioId,
            @Param("status") StatusTransacao status
    );

    @Query("SELECT t FROM Transacao t WHERE t.usuario.id = :usuarioId " +
            "AND t.metodoPagamento = :metodo ORDER BY t.data DESC")
    List<Transacao> findByUsuarioIdAndMetodoPagamento(
            @Param("usuarioId") UUID usuarioId,
            @Param("metodo") String metodo
    );


    List<Transacao> findByDescricaoContainingIgnoreCase(String descricao);
    List<Transacao> findByTipo(TipoTransacao tipo);
    List<Transacao> findByStatus(StatusTransacao status);


}