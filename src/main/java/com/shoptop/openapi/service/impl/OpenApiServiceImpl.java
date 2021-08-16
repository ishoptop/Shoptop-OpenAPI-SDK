package com.shoptop.openapi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shoptop.openapi.service.OpenApiService;
import com.shoptop.openapi.utils.OkHttpUtil;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Http请求访问API接口
 * @author: lqj
 * @date: 2021/8/5
 */
@Service
public class OpenApiServiceImpl implements com.shoptop.openapi.service.OpenApiService {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * get请求访问API接口
     * @param url
     * @param accessToken
     * @param param
     * @return
     */
    public String getApiData(String url, String accessToken, Map<String, Object> param){
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Token",accessToken);
        String result = null;
        try {
            //将参数拼接到url后面
            url = OkHttpUtil.buildHttpGet(url, param);
            result = OkHttpUtil.getData(url, headers).body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * post请求访问API接口
     * @param url
     * @param accessToken
     * @param body
     * @return
     */
    public String postApiDate(String url, String accessToken, String body){
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Token",accessToken);
        String result = null;
        try {
            result = OkHttpUtil.postJson(url, headers, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * put请求访问API接口
     * @param url
     * @param accessToken
     * @param body
     * @return
     */
    public String putApiData(String url, String accessToken, String body){
        Map<String,String> headers = new HashMap<>();
        headers.put("Access-Token", accessToken);
        String result = null;
        try {
            result = OkHttpUtil.putJson(url, headers, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * @param url
     * @param accessToken
     * @return
     */
    public String deleteApiData(String url, String accessToken) {
        Map<String,String> headers = new HashMap<>();
        headers.put("Access-Token", accessToken);
        String result = null;
        try {
            result = OkHttpUtil.deleteData(url, headers);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
