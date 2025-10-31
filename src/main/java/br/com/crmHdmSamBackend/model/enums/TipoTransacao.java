package br.com.crmHdmSamBackend.model.enums;
public enum TipoTransacao {
    ENTRADA("entrada"),
    SAIDA("saída"),
    TRANSFERENCIA("transferência");

    private final String valor;

    TipoTransacao(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }
}
