package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Usuario> findByTelefone(String telefone);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.categorias WHERE u.id = :id")
    Optional<Usuario> findByIdWithCategorias(@Param("id") UUID id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.transacoes WHERE u.id = :id")
    Optional<Usuario> findByIdWithTransacoes(@Param("id") UUID id);
}