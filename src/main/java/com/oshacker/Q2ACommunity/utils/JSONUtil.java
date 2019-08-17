package com.oshacker.Q2ACommunity.utils;

import com.alibaba.fastjson.JSONObject;

//弹窗一般返回的json串
public class JSONUtil {

    public static String getJSONString(int code) {
        JSONObject json=new JSONObject();
        json.put("code",code);
        return json.toJSONString();
    }

    public static String getJSONString(int code,String msg) {
        JSONObject json=new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }
}
