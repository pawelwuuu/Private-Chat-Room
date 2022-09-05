package com.pawelwuuu.Exceptions;

public class InvalidNicknameException extends ValidatorException{
    public InvalidNicknameException() {
        super("Passed nickname is invalid, it should consist of the letters or digits and have the length from 3 to 20 characters");
    }
}
