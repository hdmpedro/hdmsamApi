package br.com.crmHdmSamBackend.exception;

public class TentativasExcedidasException extends RuntimeException {
    public TentativasExcedidasException(String message) {
        super(message);
    }
}