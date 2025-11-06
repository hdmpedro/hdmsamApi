package br.com.crmHdmSamBackend.exception;

public class CategoriaJaExisteException extends BusinessException {
    public CategoriaJaExisteException(String nome) {
        super("Categoria já existe: " + nome);
    }
}
