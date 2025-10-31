package br.com.crmHdmSamBackend.model.dto;

public class CategoriaCreateDTO {
    private String nome;
    private String tipo;
    private String icon;

    public CategoriaCreateDTO() {}

    //getters e setters aqui
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
