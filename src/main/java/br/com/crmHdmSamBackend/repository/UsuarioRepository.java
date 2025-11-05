package br.com.crmHdmSamBackend.repository;

import br.com.crmHdmSamBackend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

//    Optional<Usuario> findByEmail(String email);
//
//    boolean existsByEmail(String email);
//
//    Optional<Usuario> findByTelefone(String telefone);
//
//    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.categorias WHERE u.id = :id")
//    Optional<Usuario> findByIdWithCategorias(@Param("id") UUID id);
//
//    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.transacoes WHERE u.id = :id")
//    Optional<Usuario> findByIdWithTransacoes(@Param("id") UUID id);

    List<Usuario> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nome, String email
    );


 //   @Query("SELECT u FROM Usuario u WHERE u.login = :login AND u.ativo = true")
   // Optional<Usuario> findByLoginAndAtivo(@Param("login") String login);


    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByLogin(String login);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);

    Optional<Usuario> findByTelefone(String telefone);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.categorias WHERE u.id = :id")
    Optional<Usuario> findByIdWithCategorias(@Param("id") UUID id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.transacoes WHERE u.id = :id")
    Optional<Usuario> findByIdWithTransacoes(@Param("id") UUID id);

    @Query("SELECT u FROM Usuario u WHERE u.login = :login AND u.ativo = true")
    Optional<Usuario> findByLoginAndAtivo(@Param("login") String login);

//    Optional<Usuario> findByLogin(String login);
//    Optional<Usuario> findByEmail(String email);
//    boolean existsByLogin(String login);
//    boolean existsByEmail(String email);
}
