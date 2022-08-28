package com.pawelwuuu.Exceptions;

public class InvalidIpException extends ValidatorException{
    public InvalidIpException() {
        super("Passed ip is in invalid format");
    }
}
