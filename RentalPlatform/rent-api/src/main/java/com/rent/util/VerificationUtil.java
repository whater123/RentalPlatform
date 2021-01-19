package com.rent.util;
import org.springframework.cache.annotation.Cacheable;
import java.util.Random;
import java.util.regex.Pattern;


/**
 * @author w
 */
public class VerificationUtil {

    private Random r = new Random();

    //    @Override
    private char randomChar() {
        // 可选字符
        String codes = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
        int index = r.nextInt(codes.length());
        return codes.charAt(index);
    }

    //    @Override
    @Cacheable(value = "verification",key = "#root.methodName+':'+#userId")
    public String getStringCode() {
        StringBuilder sb = new StringBuilder();// 用来装载生成的验证码文本
        for (int i = 0; i < 4; i++) {// 循环四次，每次生成一个字符
            String s = randomChar() + "";// 随机生成一个字母
            sb.append(s);
        };
        return sb.toString();
    }

    public String getIntCode(){
        int code = (int)((Math.random()*9+1)*1000);
        return String.valueOf(code);
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}

