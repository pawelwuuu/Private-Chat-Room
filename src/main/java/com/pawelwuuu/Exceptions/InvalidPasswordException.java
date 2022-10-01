package com.pawelwuuu.Exceptions;

/**
 * InvalidPasswordException expands ValidatorException. Throw when validation of password gone wrong. For example if
 * password contains forbidden characters or it's length is invalid.
 */
public class InvalidPasswordException extends ValidatorException{
    public InvalidPasswordException() {
            super(
                    "Passed password is invalid, it should consist of the letters and digits only and have the length from 5 to 16 characters");
    }
}
