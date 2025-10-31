package br.com.crmHdmSamBackend.model.dto;

public class CategoriaUpdateDTO {
    private String nome;
    private String icon;

    public CategoriaUpdateDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
