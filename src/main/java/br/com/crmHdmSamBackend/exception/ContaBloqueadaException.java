package br.com.crmHdmSamBackend.exception;

public class ContaBloqueadaException extends RuntimeException {
    public ContaBloqueadaException(String message) {
        super(message);
    }
}