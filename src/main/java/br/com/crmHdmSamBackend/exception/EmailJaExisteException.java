package br.com.crmHdmSamBackend.exception;

public class EmailJaExisteException extends BusinessException {
    public EmailJaExisteException(String email) {
        super("Email já está em uso: " + email);
    }
}
