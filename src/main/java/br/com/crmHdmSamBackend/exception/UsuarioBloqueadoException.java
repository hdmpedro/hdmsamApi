package br.com.crmHdmSamBackend.exception;
public class UsuarioBloqueadoException extends RuntimeException {
    public UsuarioBloqueadoException(String message) {
        super(message);
    }
}