package com.pawelwuuu.Exceptions;

/**
 * It is the core class for exceptions that come from Validator. All validator exception classes are extending this class.
 */
public abstract class ValidatorException extends RuntimeException{
    public ValidatorException(String message) {
        super(message);
    }
}
