package com.pawelwuuu;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException() {
            super("Passed password is invalid, it should consist of the letters and digits only and have the length from 6 to 16 characters");
    }
}
