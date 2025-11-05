package br.com.crmHdmSamBackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;



import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "telefone")
    private String telefone;

    @NotBlank(message = "Login é obrigatório")
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "senha")
    private String senha;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "ultimo_login")
    private OffsetDateTime ultimoLogin;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categoria> categorias = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transacao> transacoes = new ArrayList<>();

    @Column(name = "ativo")
    private boolean ativo = true;

    @Column(name = "tentativas_login")
    private int tentativasLogin = 0;

    @Column(name = "bloqueado_ate")
    private OffsetDateTime bloqueadoAte;

    @Column(name = "is_admin")
    private boolean admin = false;

    public boolean isBloqueado() {
        if (bloqueadoAte == null) {
            return false;
        }
        if (OffsetDateTime.now().isAfter(bloqueadoAte)) {
            bloqueadoAte = null;
            tentativasLogin = 0;
            return false;
        }
        return true;
    }

    public void incrementarTentativasLogin() {
        this.tentativasLogin++;
        if (this.tentativasLogin >= 5) {
            this.bloqueadoAte = OffsetDateTime.now().plusMinutes(15);
        }
    }

    public void resetarTentativasLogin() {
        this.tentativasLogin = 0;
        this.bloqueadoAte = null;
    }

    public void atualizarUltimoLogin() {
        this.ultimoLogin = OffsetDateTime.now();
    }

    public Usuario() {
    }

    public Usuario(UUID id, String nome, String email, String telefone, OffsetDateTime criadoEm, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.criadoEm = criadoEm;
        this.login = login;
        this.senha = senha;
    }

    public Usuario(UUID id, String nome, String email, String telefone, OffsetDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.criadoEm = criadoEm;
    }

    public Usuario(String nome, String mail, String telefone) {
        this.nome = nome;
        this.email = mail;
        this.telefone = telefone;
    }

    @PrePersist
    protected void onCreate() {
        if (criadoEm == null) {
            criadoEm = OffsetDateTime.now();
        }
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public List<Categoria> getCategorias() { return categorias; }
    public void setCategorias(List<Categoria> categorias) { this.categorias = categorias; }
    public List<Transacao> getTransacoes() { return transacoes; }
    public void setTransacoes(List<Transacao> transacoes) { this.transacoes = transacoes; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public OffsetDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(OffsetDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }
    public OffsetDateTime getBloqueadoAte() { return bloqueadoAte; }
    public void setBloqueadoAte(OffsetDateTime bloqueadoAte) { this.bloqueadoAte = bloqueadoAte; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public int getTentativasLogin() { return tentativasLogin; }
    public void setTentativasLogin(int tentativasLogin) { this.tentativasLogin = tentativasLogin; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}