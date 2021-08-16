package com.shoptop.openapi.service;

import java.util.Map;

/**
 *
 * @author: lqj
 * @date: 2021/8/5
 */
public interface OpenApiService {

    /**
     * get请求访问API接口
     * @param url
     * @param accessToken
     * @param param
     * @return
     */
    public String getApiData(String url, String accessToken, Map<String, Object> param);

    /**
     * post请求访问API接口
     * @param url
     * @param accessToken
     * @param body
     * @return
     */
    public String postApiDate(String url, String accessToken, String body);

    /**
     * put请求访问API接口
     * @param url
     * @param accessToken
     * @param body
     * @return
     */
    public String putApiData(String url, String accessToken, String body);

    /**
     * delete请求访问API接口
     * @param url
     * @param accessToken
     * @return
     */
    public String deleteApiData(String url, String accessToken);
}
