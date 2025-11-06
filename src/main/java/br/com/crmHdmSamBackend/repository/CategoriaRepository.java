package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.Categoria;
import br.com.crmHdmSamBackend.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    List<Categoria> findByUsuarioId(UUID usuarioId);

    List<Categoria> findByUsuarioIdAndTipo(UUID usuarioId, TipoTransacao tipo);


    Optional<Categoria> findByUsuarioIdAndNome(UUID usuarioId, String nome);

    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId AND c.tipo = :tipo ORDER BY c.nome ASC")
    List<Categoria> findByUsuarioIdAndTipoOrderByNome(
            @Param("usuarioId") UUID usuarioId,
            @Param("tipo") TipoTransacao tipo
    );

    boolean existsByUsuarioIdAndNome(UUID usuarioId, String nome);

    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.usuario.id = :usuarioId")
    Long countByUsuarioId(@Param("usuarioId") UUID usuarioId);
}