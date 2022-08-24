package com.pawelwuuu.Exceptions;

public class InvalidNicknameException extends RuntimeException{
    public InvalidNicknameException() {
        super("Passed nickname is invalid, it should consist of the letters and digits only and have the length from 3 to 20 characters");
    }
}
