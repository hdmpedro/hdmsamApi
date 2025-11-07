package br.com.crmHdmSamBackend.model.dto.api;

import jakarta.validation.constraints.NotBlank;

public class RenovarRequest {

    @NotBlank(message = "Token para atualiação é obrigatório")
    private String renovarToken;

    public RenovarRequest() {
    }

    public RenovarRequest(String renovarToken) {
        this.renovarToken = renovarToken;
    }

    public String getRenovarToken() {
        return renovarToken;
    }

    public void setRenovarToken(String renovarToken) {
        this.renovarToken = renovarToken;
    }
}