package com.pawelwuuu;

public class InvalidIpException extends RuntimeException{
    public InvalidIpException() {
        super("Passed ip is in invalid format");
    }
}
