package com.shoptop.openapi;

import com.alibaba.fastjson.JSONObject;
import com.shoptop.openapi.model.enums.HookTopicEnum;
import com.shoptop.openapi.service.OpenApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * OpenAPI访问demo
 * @author: lqj
 * @date: 2021/8/7
 */
@SpringBootTest
public class OpenApiDemo {
    /**
     * @param url 协议+店铺域名+/openapi/v1 + URI
     * eg: https://xiaofeng1.shoptop.tech/openapi/v1/orders/count
     * @param accessToken
     */
    @Autowired
    OpenApiService openApiService;

    /**
     * 测试get请求访问API接口
     */
    @Test
    public void testGetApiData(){
        String url = "https://xxx.ishoptop.com/openapi/v1/orders";
        //访问token
        String accessToken = "************************";
        //参数map
        Map<String, Object> param = new HashMap<>();
        param.put("pageNo",1);
        param.put("pageSize",10);
        //参数封装为map,调用该方法会将参数拼接到url后
        String apiData = openApiService.getApiData(url, accessToken, param);
        System.out.println("####################################### 请求结果 #######################################\n " + apiData +"\n######################################################################################");
    }

    /**
     * 测试post请求访问API接口
     * 数据提交格式为json字符串
     */
    @Test
    public void testPostApiData(){
        String url = "https://xxx.ishoptop.com/openapi/v1/webhooks";
        //访问token
        String accessToken = "************************";
        //参数map
        Map<String, Object> param = new HashMap<>();
        param.put("address","http://localhost:8080/webhooks");
        param.put("topic", HookTopicEnum.PRODUCTS_UPDATE.getCode());
        String apiData = openApiService.postApiDate(url, accessToken, JSONObject.toJSON(param).toString());
        System.out.println("####################################### 请求结果 #######################################\n " + apiData +"\n######################################################################################");
    }

    /**
     * 测试put请求访问API接口
     * 数据提交格式为json字符串
     */
    @Test
    public void testPutApiData(){
        String url = "https://xxx.ishoptop.com/openapi/v1/webhooks";
        //访问token
        String accessToken = "************************";
        //参数map
        Map<String, Object> param = new HashMap<>();
        param.put("id","1423672312586321921");
        param.put("address","http://localhost:8080/webhooks");
        param.put("topic", HookTopicEnum.PRODUCTS_UPDATE.getCode());
        String apiData = openApiService.putApiData(url, accessToken, JSONObject.toJSON(param).toString());
        System.out.println("####################################### 请求结果 #######################################\n " + apiData +"\n######################################################################################");
    }

    /**
     * 测试delete请求访问API接口
     */
    @Test
    public void testDeleteApiData(){
        String url = "https://xxx.ishoptop.com/openapi/v1/webhooks";
        //访问token
        String accessToken = "************************";
        String id = "1423879039311233025";
        url = url + "/" + id;
        String apiData = openApiService.deleteApiData(url, accessToken);
        System.out.println("####################################### 请求结果 #######################################\n " + apiData +"\n######################################################################################");
    }

}
