package com.pawelwuuu.Exceptions;

/**
 * InvalidNicknameException expands ValidatorException. Throw when validation of nickname gone wrong. For example, when nickname
 * is longer or shorter than it should be, also if it contains forbidden characters.
 */
public class InvalidNicknameException extends ValidatorException{
    public InvalidNicknameException() {
        super("Passed nickname is invalid, it should consist of the letters or digits and have the length from 3 to 20 characters");
    }
}
