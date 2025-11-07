package br.com.crmHdmSamBackend.exception;

public class UsuarioInativoException extends RuntimeException {
    public UsuarioInativoException(String message) {
        super(message);
    }
}