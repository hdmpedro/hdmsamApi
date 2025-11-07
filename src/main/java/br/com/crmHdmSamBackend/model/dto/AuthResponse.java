package br.com.crmHdmSamBackend.model.dto;

public class AuthResponse {

    private String tokenAcesso;
    private String tokenRenovacao;
    private String tipoToken;
    private Long expiraEm;

    public AuthResponse() {
    }

    public AuthResponse(String tokenAcesso, String tokenRenovacao, String tipoToken, Long expiraEm) {
        this.tokenAcesso = tokenAcesso;
        this.tokenRenovacao = tokenRenovacao;
        this.tipoToken = tipoToken;
        this.expiraEm = expiraEm;
    }

    public String getTokenAcesso() {
        return tokenAcesso;
    }

    public void setTokenAcesso(String tokenAcesso) {
        this.tokenAcesso = tokenAcesso;
    }

    public String getTokenRenovacao() {
        return tokenRenovacao;
    }

    public void setTokenRenovacao(String tokenRenovacao) {
        this.tokenRenovacao = tokenRenovacao;
    }

    public String getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(String tipoToken) {
        this.tipoToken = tipoToken;
    }

    public Long getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(Long expiraEm) {
        this.expiraEm = expiraEm;
    }
}