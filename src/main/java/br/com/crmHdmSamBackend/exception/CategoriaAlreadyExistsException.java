package br.com.crmHdmSamBackend.exception;

public class CategoriaAlreadyExistsException extends BusinessException {
    public CategoriaAlreadyExistsException(String nome) {
        super("Categoria já existe: " + nome);
    }
}
