package com.rent.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rent.constant.SystemConstant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author MSI-PC
 */
public class MyUtil {
    public static boolean strHasVoid(String...strs){
        for (String s :
                strs) {
            if (s == null || "".equals(s)){
                return true;
            }
        }
        return false;
    }

    public static boolean intHasVoid(int...ints){
        for (int i :
                ints) {
            if(i == 0){
                return true;
            }
        }
        return false;
    }

    public static boolean jsonHasVoid(String json,String...strs){
        JSONObject jsonObject = JSON.parseObject(json);
        for (String s:
                strs) {
            if("".equals(jsonObject.get(s))||jsonObject.get(s) == null){
                return true;
            }
        }
        return false;
    }

    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$";

        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }

    public static boolean isPhoneNumber(String phone) {
        Pattern pattern = Pattern.compile("^(13[0-9]|15[0-9]|153|15[6-9]|180|18[23]|18[5-9])\\d{8}$");
        Matcher matcher = pattern.matcher(phone);

        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static String getNowTime(){
        return  new SimpleDateFormat(SystemConstant.DATE_FORMAT).format(new Date());
    }

    public static boolean isAllRuleMoney(String...moneys){
        for (String s:
                moneys) {
            if(!MoneyUtil.isRuleString(s)){
                return false;
            }
        }
        return true;
    }

}
