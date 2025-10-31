package br.com.crmHdmSamBackend.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TransacaoDTO {
    private UUID id;
    private UUID idUsuario;
    private String telefone;
    private String tipo;
    private String categoria;
    private String descricao;
    private BigDecimal quantia;
    private OffsetDateTime data;
    private String metodoPagamento;
    private String status;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public TransacaoDTO() {}

    public TransacaoDTO(UUID id, UUID idUsuario, String telefone, String tipo, String categoria,
                        String descricao, BigDecimal quantia, OffsetDateTime data, String metodoPagamento,
                        String status, OffsetDateTime criadoEm, OffsetDateTime atualizadoEm) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.telefone = telefone;
        this.tipo = tipo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.quantia = quantia;
        this.data = data;
        this.metodoPagamento = metodoPagamento;
        this.status = status;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getQuantia() { return quantia; }
    public void setQuantia(BigDecimal quantia) { this.quantia = quantia; }
    public OffsetDateTime getData() { return data; }
    public void setData(OffsetDateTime data) { this.data = data; }
    public String getMetodoPagamento() { return metodoPagamento; }
    public void setMetodoPagamento(String metodoPagamento) { this.metodoPagamento = metodoPagamento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
    public OffsetDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(OffsetDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
