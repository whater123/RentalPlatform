package com.rent.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rent.constant.SystemConstant;

import java.text.ParseException;
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
        return  new SimpleDateFormat(SystemConstant.DATETIME_FORMAT).format(new Date());
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
    /**
     *计算两个日期相隔的天数
      */
    public static int nDaysBetweenTwoDate(String firstString,String secondString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date firstDate=null;
        Date secondDate=null;
        try {
            firstDate = df.parse(firstString);
            secondDate=df.parse(secondString);
        }
        catch(Exception e) {
            // 日期型字符串格式错误
            System.out.println("日期型字符串格式错误");
        }

        assert secondDate != null;
        return (int)((secondDate.getTime()-firstDate.getTime())/(24*60*60*1000));
    }

    /**
     *获取某个日期加上一定天数后的日期
     */
    public static String addDate(String timeParam, long day){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // 日期格式
        Date date = null; // 指定日期
        try {
            date = dateFormat.parse(timeParam);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("日期型字符串格式错误");
        }
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        Date newDate = new Date(time);
        return dateFormat.format(newDate); // 将毫秒数转换成日期
    }
}
