package com.shoptop.openapi.model.enums;

import lombok.Getter;

/**
 * 事件类型枚举类
 * @author: lqj
 * @date: 2021/7/30
 */
@Getter
public enum HookTopicEnum {

    APP_EXPIRED("app/expired","应用到期"),
    APP_UNINSTALLED("app/uninstalled","卸载应用"),
    COLLECTIONS_CREATE("collections/create","创建专辑"),
    COLLECTIONS_DELETE("collections/delete","删除专辑"),
    COLLECTIONS_UPDATE("collections/update","更新专辑"),
    CUSTOMERS_CREATE("customers/create","创建顾客"),
    CUSTOMERS_DELETE("customers/delete","删除顾客"),
    CUSTOMERS_UPDATE("customers/update","更新顾客"),
    FULFILLMENTS_CREATE("fulfillments/create","创建运单"),
    FULFILLMENTS_UPDATE("fulfillments/update","更新运单"),
    ORDERS_CANCELLED("orders/cancelled","取消订单"),
    ORDERS_CREATE("orders/create","创建订单"),
    ORDERS_DELETE("orders/delete","删除订单"),
    ORDERS_FINISHED("orders/finished","完成订单"),
    ORDERS_PAID("orders/paid","支付订单"),
    ORDERS_FULFILLED("orders/fulfilled","全额付款"),
    ORDERS_PARTIALLY_FULFILLED("orders/partially_fulfilled","部分付款"),
    ORDERS_REFUNDED("orders/refunded","订单退款"),
    ORDERS_UPDATE("orders/update","更新订单"),
    PRODUCTS_CREATE("products/create","创建商品"),
    PRODUCTS_DELETE("products/delete","删除商品"),
    PRODUCTS_UPDATE("products/update","更新商品"),
    ;

    // 代码
    private String code;
    // 描述
    private String desc;

    HookTopicEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
