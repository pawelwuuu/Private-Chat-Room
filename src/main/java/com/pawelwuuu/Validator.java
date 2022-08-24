package com.pawelwuuu;

public class Validator {
    public static boolean isNicknameCorrect(String nick){
        String nickRegex = "\\w+";

        if(nick.matches(nickRegex) && nick.length() > 2 && nick.length() < 21) {
            return true;
        }

        return false;
    }

    public static boolean isPasswordCorrect(String password){
        String passwordRegex = "\\w+";

        if(password.matches(passwordRegex) && password.length() >= 5 && password.length() < 17) {
            return true;
        }

        return false;
    }

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
