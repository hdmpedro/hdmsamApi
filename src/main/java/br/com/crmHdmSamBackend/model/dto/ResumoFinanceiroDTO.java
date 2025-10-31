package br.com.crmHdmSamBackend.model.dto;

import java.math.BigDecimal;

public class ResumoFinanceiroDTO {
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;
    private BigDecimal saldo;
    private Long totalTransacoes;
    private Long transacoesPendentes;

    public ResumoFinanceiroDTO() {}

    public ResumoFinanceiroDTO(BigDecimal totalEntradas, BigDecimal totalSaidas, BigDecimal saldo,
                               Long totalTransacoes, Long transacoesPendentes) {
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
        this.saldo = saldo;
        this.totalTransacoes = totalTransacoes;
        this.transacoesPendentes = transacoesPendentes;
    }

    public BigDecimal getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(BigDecimal totalEntradas) { this.totalEntradas = totalEntradas; }
    public BigDecimal getTotalSaidas() { return totalSaidas; }
    public void setTotalSaidas(BigDecimal totalSaidas) { this.totalSaidas = totalSaidas; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public Long getTotalTransacoes() { return totalTransacoes; }
    public void setTotalTransacoes(Long totalTransacoes) { this.totalTransacoes = totalTransacoes; }
    public Long getTransacoesPendentes() { return transacoesPendentes; }
    public void setTransacoesPendentes(Long transacoesPendentes) { this.transacoesPendentes = transacoesPendentes; }
}
