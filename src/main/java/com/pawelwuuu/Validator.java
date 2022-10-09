package com.pawelwuuu;

/**
 * Class with static methods that is used for various validation.
 */
public class Validator {
    /**
     * Determines if nick is in correct form. It should be one word with length 3 to 20 characters. It can consist of
     * numbers.
     * @param nick string containing nickname.
     * @return true if validation is successful, otherwise false.
     */
    public static boolean isNicknameCorrect(String nick){
        String nickRegex = "\\w+";

        if(nick.matches(nickRegex) && nick.length() > 2 && nick.length() < 21) {
            return true;
        }

        return false;
    }

    /**
     * Determines if password is in correct form. It should be one word with length 5 to 16 characters. It can consist of
     * numbers.
     * @param password string with password
     * @return true if validation is successful, otherwise false.
     */
    public static boolean isPasswordCorrect(String password){
        String passwordRegex = "\\w+";

        if(password.matches(passwordRegex) && password.length() >= 5 && password.length() < 17) {
            return true;
        }

        return false;
    }

    /**
     * Determines if ip is in correct form. It should be in form that is used by international terms.
     * @param Ip string with ip
     * @return true if validation is successful, otherwise false.
     */
    public static boolean IsIpCorrect(String Ip){
        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";

        String ipRegex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        if(Ip.matches(ipRegex)) {
            return true;
        }

        return false;
    }

}
