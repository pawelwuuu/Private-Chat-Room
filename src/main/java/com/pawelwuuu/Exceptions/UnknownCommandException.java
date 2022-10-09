package com.pawelwuuu.Exceptions;

/**
 * UnknownCommandException expands ValidatorException. Thrown when incorrect command is typed. For example, it's syntax
 * is wrong or such command doesn't exist.
 */
public class UnknownCommandException extends Exception{
    public UnknownCommandException(String message) {
        super(message);
    }
}
