package br.com.crmHdmSamBackend.exception;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(String email) {
        super("Email já está em uso: " + email);
    }
}
