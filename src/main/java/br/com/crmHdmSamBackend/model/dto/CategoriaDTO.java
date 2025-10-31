package br.com.crmHdmSamBackend.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CategoriaDTO {
    private UUID id;
    private UUID idUsuario;
    private String nome;
    private String tipo;
    private String icon;
    private OffsetDateTime criadoEm;

    public CategoriaDTO() {}

    public CategoriaDTO(UUID id, UUID idUsuario, String nome, String tipo, String icon, OffsetDateTime criadoEm) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.tipo = tipo;
        this.icon = icon;
        this.criadoEm = criadoEm;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getIdUsuario() { return idUsuario; }
    public void setIdUsuario(UUID idUsuario) { this.idUsuario = idUsuario; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
