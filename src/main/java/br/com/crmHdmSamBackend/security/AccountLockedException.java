package br.com.crmHdmSamBackend.security;

import org.springframework.security.core.AuthenticationException;
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}