package br.com.crmHdmSamBackend.model.dto.api;


import java.time.OffsetDateTime;
import java.util.UUID;

public class TokenInfoDTO {

    private UUID id;
    private OffsetDateTime criadoEm;
    private OffsetDateTime expiraEm;
    private boolean revogado;

    public TokenInfoDTO() {
    }

    public TokenInfoDTO(UUID id, OffsetDateTime criadoEm, OffsetDateTime expiraEm, boolean revogado) {
        this.id = id;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
        this.revogado = revogado;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(OffsetDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public OffsetDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(OffsetDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    public boolean isRevogado() {
        return revogado;
    }

    public void setRevogado(boolean revogado) {
        this.revogado = revogado;
    }
}
