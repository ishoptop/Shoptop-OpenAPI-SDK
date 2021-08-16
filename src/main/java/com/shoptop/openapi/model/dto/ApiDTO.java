package com.shoptop.openapi.model.dto;

public class ApiDTO {
    /**
     * url前缀 ( 协议+店铺域名+/openapi/v1 )
     * eg:  https://xiaofeng1.shoptop.tech/openapi/v1
     */
    private String prefix;

    /**
     * uri路径
     * eg:/orders/count
     */
    private String uri;

    /**
     * 访问token
     */
    private String accessToken;
}
