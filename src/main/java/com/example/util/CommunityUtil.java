package com.example.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // hello -> abc123def456
    // hello + 3e4a8 -> abc123def456abc
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }


    /**
     * 获取cookie的值
     *
     * @param request 请求域
     * @param name    想要获取的cookie名字
     * @return cookie的值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        if (request == null || request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies())
            if (cookie != null && cookie.getName().equals(name)) return cookie.getValue();
        return null;
    }


    public static String getJsonString(int code) {
        return getJsonString(code, null);
    }

    public static String getJsonString(int code, String msg) {
        return getJsonString(code, msg, null);
    }

    public static String getJsonString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map!=null)
            for (String str : map.keySet()) json.put(str, map.get(str));
        return json.toJSONString();
    }




}











