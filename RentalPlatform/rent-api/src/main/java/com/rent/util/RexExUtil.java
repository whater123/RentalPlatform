package com.rent.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RexExUtil {
    public static boolean isContainNumber(String str) {

        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isContainAlphabet(String str) {

        Pattern p = Pattern.compile("^[A-Za-z]+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean isRulePassword(String str) {

        Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9~!@&%#_]{8,16}$");
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return true;
        }
        return false;
    }


    public static boolean isRealNumber(String str){
        if(str.charAt(0) == '-'){
            str = str.substring(1);
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
