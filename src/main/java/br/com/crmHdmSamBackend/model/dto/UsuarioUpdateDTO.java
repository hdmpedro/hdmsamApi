package br.com.crmHdmSamBackend.model.dto;

public class UsuarioUpdateDTO {
    private String nome;
    private String telefone;

    public UsuarioUpdateDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
