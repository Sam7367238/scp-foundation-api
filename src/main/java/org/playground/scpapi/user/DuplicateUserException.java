package org.playground.scpapi.user;

@Deprecated(since = "0.7.0", forRemoval = true)
public class DuplicateUserException extends RuntimeException {
    public DuplicateUserException() {
        super("A user is already registered");
    }
}
