package com.pawelwuuu.Exceptions;

public abstract class ValidatorException extends RuntimeException{
    public ValidatorException(String message) {
        super(message);
    }
}
