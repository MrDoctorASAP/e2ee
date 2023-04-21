package com.e2ee.api.service.exceptons;

import lombok.Getter;

import static java.lang.String.format;

public class UserAlreadyExistsException extends AuthException {

    private static final String MESSAGE_FORMAT = "Username %s already exists";

    @Getter
    private final String username;

    public UserAlreadyExistsException(String username, Throwable cause) {
        super(MESSAGE_FORMAT.formatted(username), cause);
        this.username = username;
    }

    public UserAlreadyExistsException(String username) {
        super(MESSAGE_FORMAT.formatted(username));
        this.username = username;
    }

}
