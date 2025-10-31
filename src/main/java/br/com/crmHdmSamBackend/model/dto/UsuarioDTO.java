package br.com.crmHdmSamBackend.model.dto;


import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class UsuarioDTO {
    private UUID id;
    private String nome;
    private String email;
    private String telefone;
    private OffsetDateTime criadoEm;

    public UsuarioDTO() {}

    public UsuarioDTO(UUID id, String nome, String email, String telefone, OffsetDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.criadoEm = criadoEm;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}

