package com.pawelwuuu.Exceptions;

/**
 * InvalidIpException expands ValidatorException. Throw when ip passed to client is invalid. For example passed ip is in
 * form without dots or with dots as separators but numeric values are out of range 0-254. It is thrown also if first digit
 * is 0.
 */
public class InvalidIpException extends ValidatorException{
    public InvalidIpException() {
        super("Passed ip is in invalid format");
    }
}