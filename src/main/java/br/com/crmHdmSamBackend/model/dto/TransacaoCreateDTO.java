package br.com.crmHdmSamBackend.model.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransacaoCreateDTO {
    private String telefone;
    private String tipo;
    private String categoria;
    private String descricao;
    private BigDecimal quantia;
    private OffsetDateTime data;
    private String metodoPagamento;
    private String status;

    public TransacaoCreateDTO() {}

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
}
