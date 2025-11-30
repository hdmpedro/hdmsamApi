package br.com.crmHdmSamBackend.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "api_rate_limit")
public class ApiRateLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String identificador;

    @Column(nullable = false)
    private String endpoint;

    @Column(name = "requisicoes", nullable = false)
    private int requisicoes;

    @Column(name = "janela_inicio", nullable = false)
    private OffsetDateTime janelaInicio;

    public ApiRateLimit() {
    }

    public ApiRateLimit(String identificador, String endpoint) {
        this.identificador = identificador;
        this.endpoint = endpoint;
        this.requisicoes = 1;
        this.janelaInicio = OffsetDateTime.now();
    }

    public void incrementar() {
        this.requisicoes++;
    }

    public void resetar() {
        this.requisicoes = 1;
        this.janelaInicio = OffsetDateTime.now();
    }

    public boolean janelaExpirou(long minutos) {
        return OffsetDateTime.now().isAfter(janelaInicio.plusMinutes(minutos));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getRequisicoes() {
        return requisicoes;
    }

    public void setRequisicoes(int requisicoes) {
        this.requisicoes = requisicoes;
    }

    public OffsetDateTime getJanelaInicio() {
        return janelaInicio;
    }

    public void setJanelaInicio(OffsetDateTime janelaInicio) {
        this.janelaInicio = janelaInicio;
    }
}
