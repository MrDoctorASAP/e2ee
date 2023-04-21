package com.e2ee.api.service.exceptons;

import org.springframework.security.core.AuthenticationException;

public class AuthException extends AuthenticationException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

}
