package com.rent.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * @author MSI-PC
 */
public class ExpressUtil {
    public static String queryExpress(String no,String type) {
        // 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String host = "https://wuliu.market.alicloudapi.com";
        // 【2】后缀
        String path = "/kdi";
        // 【3】开通服务后 买家中心-查看AppCode
        String appcode = "8c9484bb8a924da087924c5fa11a20a9";
        // 【4】拼接请求链接
        String urlSend = host + path + "?no=" + no +"&type="+type;
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            // 格式Authorization:APPCODE (中间是英文空格)
            httpURLCon .setRequestProperty("Authorization", "APPCODE " + appcode);
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
                System.out.println("正常请求计费(其他均不计费)");
                System.out.println("获取返回的json:");
                System.out.print(json);
                return json;
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && "Invalid AppCode `not exists`".equals(error)) {
                    return "AppCode错误 ";
                } else if (httpCode == 400 && "Invalid Url".equals(error)) {
                    return "请求的 Method、Path 或者环境错误";
                } else if (httpCode == 400 && "Invalid Param Location".equals(error)) {
                    return "参数错误";
                } else if (httpCode == 403 && "Unauthorized".equals(error)) {
                    return "服务未被授权（或URL和Path不正确）";
                } else if (httpCode == 403 && "Quota Exhausted".equals(error)) {
                    return "套餐包次数用完";
                } else {
                    return "参数名错误 或 其他错误\n" + error;
                }
            }
        } catch (MalformedURLException e) {
            return "URL格式错误";
        } catch (UnknownHostException e) {
            return "URL地址错误";
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            e.printStackTrace();
            return "服务器错误";
        }

    }

    /**
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
