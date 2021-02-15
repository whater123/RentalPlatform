package com.rent.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoneyUtil {

    public static boolean isRuleString(String str) {
        if(!str.contains(".")){
            return false;
        }
        if(str.substring(str.indexOf('.')).length() > 3){
            return false;
        }
        String[] strings = str.split("\\.");
        if(strings.length != 2){
            return false;
        }
        if(RexExUtil.isRealNumber(strings[0]) && RexExUtil.isRealNumber(strings[1])){
            return true;
        }else{
            return false;
        }
    }

    public static String addTail(String str){
        if(!str.contains(".")){
            str = str + ".00";
        }
        while(str.substring(str.indexOf('.')).length() < 3){
            str = str + "0";
        }
        return str;
    }

    private static List<String> fractionSplit(String str){
        String[] strings = str.split("\\.");
        return new ArrayList<String>(Arrays.asList(strings));
    }

    public static String fractionAdd(String s1,String s2) throws Exception {
        s1 = addTail(s1);
        s2 = addTail(s2);
        if(!isRuleString(s1) || !isRuleString(s2)){
            throw new Exception("参数不合法，必须是小数形式，且小数点必须小于或等于两位。");
        }
        return new BigDecimal(s1).add(new BigDecimal(s2)).toString();
    }

    public static String fractionSubtract(String s1,String s2) throws Exception {
        s1 = addTail(s1);
        s2 = addTail(s2);
        if(!isRuleString(s1) || !isRuleString(s2)){
            throw new Exception("参数不合法，必须是小数形式，且小数点必须小于或等于两位。");
        }
        return new BigDecimal(s1).subtract(new BigDecimal(s2)).toString();
    }

    public static String fractionMultiply(String s1,String s2) throws Exception {
        s1 = addTail(s1);
        s2 = addTail(s2);
        if(!isRuleString(s1) || !isRuleString(s2)){
            throw new Exception("参数不合法，必须是小数形式，且小数点必须小于或等于两位。");
        }
        return new BigDecimal(s1).multiply(new BigDecimal(s2)).setScale(2, RoundingMode.HALF_UP).toString();
    }

    public static String fractionDivide(String s1,String s2) throws Exception {
        s1 = addTail(s1);
        s2 = addTail(s2);
        if(!isRuleString(s1) || !isRuleString(s2)){
            throw new Exception("参数不合法，必须是小数形式，且小数点必须小于或等于两位。");
        }
        //取小数点后两位
        return new BigDecimal(s1).divide(new BigDecimal(s2),2, RoundingMode.HALF_UP).toString();
    }

    /**
     * @return 返回值      含义
     *          -1        小于
     *          0         等于
     *          1         大于
     */
    public static int compare(String s1,String s2) throws Exception {
        s1 = addTail(s1);
        s2 = addTail(s2);
        if(!isRuleString(s1) || !isRuleString(s2)){
            throw new Exception("参数不合法，必须是小数形式，且小数点必须小于或等于两位。");
        }
        return new BigDecimal(s1).compareTo(new BigDecimal(s2));
    }
}
