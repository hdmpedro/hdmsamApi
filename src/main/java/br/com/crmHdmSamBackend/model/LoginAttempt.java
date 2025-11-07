package br.com.crmHdmSamBackend.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_attempts")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String login;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private boolean sucesso;

    @Column(name = "tentativa_em", nullable = false)
    private OffsetDateTime tentativaEm;

    public LoginAttempt() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public OffsetDateTime getTentativaEm() {
        return tentativaEm;
    }

    public void setTentativaEm(OffsetDateTime tentativaEm) {
        this.tentativaEm = tentativaEm;
    }
}