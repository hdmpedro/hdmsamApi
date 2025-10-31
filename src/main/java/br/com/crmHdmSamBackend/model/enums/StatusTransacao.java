package br.com.crmHdmSamBackend.model.enums;

public enum StatusTransacao {
    PENDENTE("pendente"),
    CONFIRMADA("confirmada"),
    CANCELADA("cancelada");

    private final String valor;

    StatusTransacao(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}