package br.com.crmHdmSamBackend.model;


import jakarta.persistence.*;

import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
public class TokenRenovacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "expira_em", nullable = false)
    private OffsetDateTime expiraEm;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(nullable = false)
    private boolean revogado = false;

    public TokenRenovacao() {
    }

    public TokenRenovacao(String token, Usuario usuario, OffsetDateTime expiraEm) {
        this.token = token;
        this.usuario = usuario;
        this.expiraEm = expiraEm;
    }

    public boolean isExpirado() {
        return OffsetDateTime.now().isAfter(expiraEm);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public OffsetDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(OffsetDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isRevogado() {
        return revogado;
    }

    public void setRevogado(boolean revogado) {
        this.revogado = revogado;
    }
}