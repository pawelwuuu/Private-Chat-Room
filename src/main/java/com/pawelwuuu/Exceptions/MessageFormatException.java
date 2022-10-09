package com.pawelwuuu.Exceptions;

/**
 * InvalidMessageException expands ValidatorException. Throw when validation of message gone wrong,
 */
public class MessageFormatException extends Exception{
    public MessageFormatException(String message) {
        super(message);
    }
}
