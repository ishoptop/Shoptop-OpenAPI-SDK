# 开放平台简介

## 平台概述

​      **Shoptop**开放平台是为广大应用开发者提供的平台，开发者可以借助平台的商业合作模式、扩展平台的内置功能，开放平台提供的API允许商家授权合作伙伴读取或写入商家的数据从而与其它系统和平台进行交互从而为商家服务。

## 使用范围

- 商户自身开放人员
- 第三方开发人员

## 使用要求

- 所有API均需遵守**Shoptop API**许可和使用条款
- 所有API均受速率限制
- 所有API都要求开发人员进行身份验证
- 所有API已版本化

## 开发者权限申请

开发者在使用openAPI的过程中会需要申请相关资格和使用权限。

### 客户端资格申请

您的插件需要获得API key和API secret key，以便在授权过程中用于表明您的身份。 请通过邮件(**[support@shoptop.com](mailto:support@shoptop.com)**)联系Shoptop的开发以获取API Key 和API Secret Key，在这个过程中，你需要提供App的基本信息以及一个接口的地址（Oauth回调接口 redirect_uri)，如下：

**公司资料**

- 公司业务简介（请附上官网地址）

Public APP基础信息

1. 授权回调地址(redirect_uri)
3. APP图标（尺寸 150x150，PNG格式）
4. 接口人的邮箱地址
5. APP中英文名称
6. APP的中英文简要描述
7. APP权限：店铺信息、顾客、订单、商品 (具体见开发者文档)etc.

### 开发者店铺使用权申请

开发者在开发第三方插件的时候会需要申请店铺的使用权，每次申请的期限不能超过60天，用于测试自己开发的插件功能。 开发者需要提供以下信息，并发邮件([support@shoptop.com](mailto:support@shoptop.com))给Shoptop的开发申请权限。

1. 店铺ID
2. 时长(单位天)
3. 申请的目的
4. 插件名称

过期后如果需更多时间做测试使用，可以继续通过邮件发起申请。

## API身份验证

为了确保平台上的数据安全可靠，进行API调用时，所有与我们API连接的插件都必须进行身份验证

### 认证类型

平台对插件进行身份验证的方法有两种：

- **OAuth**
- **Access-Token**验证

与平台连接的任何Web应用程序或服务都被称为“插件”，无论其如何向最终用户公开。不同类型的插件使用不同的身份验证方法。

- **公共插件**使用OAuth
- **私有插件**使用Access-Token验证

在开始开发过程之前，请确保您了解两种身份验证方案之间的区别。

### 使用OAuth进行身份验证

公共插件必须使用OAuth 2.0规范进行身份验证，才能使用Shoptop的API资源

#### 术语

在详细了解授权过程的详细信息之前，请确保您熟悉本文档中使用的一些关键术语：

| 名称   | 描述                                                         |
| ------ | ------------------------------------------------------------ |
| 插件   | 与平台对接的任何应用统称为插件。                             |
| 客户端 | 想要访问商家数据的任何插件。                                 |
| API    | Shoptop提供的 API，客户端 可以使用 API 读取和修改店铺数据。  |
| 用户   | Shoptop 的账户持有人，通常是商家。用户 授权 客户端 通过 API 访问店铺数据。 |

#### OAuth流程

Shoptop使用OAuth 2.0的授权代码授予流程代表用户发放访问令牌。

1. 用户请求安装该插件。
2. 该插件重定向到Shoptop并加载OAuth授权页面和插件的访问范围。
3. 用户同意授权并重定向到插件的 redirect_uri。
4. 插件发出包括client_id，client_secret和code的请求到Shoptop
5. Shoptop返回token
6. 该插件使用token向Shoptop发出API请求。
7. Shoptop返回请求的数据

**第一步：获取客户端凭据**

您需要获得API key和API secret key，以便在授权过程中用于表明您的身份。 获取客户端资格：请联系Shoptop的开发以获取API Key 和API Secret Key，在这个过程中，你需要提供App的基本信息以及两个接口的地址（Oauth 触发接口app_uri,和Oauth回调接redirect_uri)

**第二步：请求获得许可**

已登录平台的商家在应用市场点击安装您的插件时，Shoptop会首先以Get方式调用您提供的触发接口app_uri，app_uri接口需要跳转至以下地址进行授权：

```bash
https://{store_name}.ishoptop.com/admin/oauth/authorize?client_id={api_key}&scope={scopes}&redirect_uri={redirect_uri}&response_type={response_type}
```

- **store_name**：店铺名称，通常是店家域名的slug
- **api_key**：应用的API密钥
- **scope**：空格分隔的scope列表替换它。 例如，要编写订单和读取顾客信息，请使用`scope=write_order read_customer`(scope之间以空格分开,[查看全部scope列表](#scopes))
- **redirect_uri**： 您在授权客户端后重定向到应用的URL
- **response_type**：此请求下的值为code

**第三步：确认授权**

当用户单击提示中的“授权”按钮时，它们将被重定向到上面指定的客户端地址。重定向中传递的参数。

```bash
http://example.com/some/redirect_uri?code={authorization_code}&shop={store_name}.ishoptop.com&hmac={hmac}
```

在继续接下来的流程之前，请确保您的插件执行了以下安全检查。 如果检查失败，您的插件须拒绝该请求并返回错误

- 校验hmac字段，确认其值正确([Hmac生成算法](#hmac))
- 该hmac是有效的。HMAC由Shoptop签名
- 该shop参数是有效的主机名，如store.ishoptop.com

如果所有安全检查均通过，则可以通过向商店的access_token端点发送请求，用授权 code 交换为一个维持时间一年的access_token和不过期的refresh_token

```bash
POST https://{store_name}.ishoptop.com/admin/oauth/token
```

该请求需要包含如下所示参数

- **client_id**：插件的 API key
- **client_secret**：插件的 API secret key
- **code**：回调地址中提供的授权code
- **grant_type**：授权方式，该请求下的值为authorization_code
- **redirect_uri**：应用的回调地址

请求成功后的返回值：

```json
{
  "token_type": "Bearer",
  "expires_at": 1550546245,
  "access_token": "eyJ0eXAiOiJKV1QiLCJh",
  "refresh_token": "def502003d28ba08a964e",
  "store_id": "2",
  "store_name": "xiong1889"
}
```

- **token_type**：返回Bearer
- **expires_at**：访问密钥过期时间
- **access_token**：访问密钥
- **refresh_token**：更新密钥，用于更新密钥
- **store_id**：店铺id
- **store_name**：用户名，通常是店家域名的slug

access_token 失效之后，请使用下面的请求完成更新access_token的操作（您必须保存更新之后的refresh_token已方便再次进行更新）

```bash
POST https://{store_name}.ishoptop.com/admin/oauth/token
```

该请求需要包含如下所示参数

- **client_id**：插件的 API key
- **client_secret**：插件的 API secret key
- **refresh_token**: 保存在您插件中的refresh_token值
- **grant_type**：授权方式，该请求下的值为refresh_token
- **redirect_uri**：插件的回调地址

**第四步：调用 API**

客户端获得API访问token后，便可以向RESTful API发出经过身份验证的请求。这些请求 Headers 必须带有 Access-Token: {access_token}

请求商品数据示例如下：

```bash
curl -i -X GET \
     -H "Content-Type:application/json" \
     -H "Access-Token:B_x-_5aVeXNwI-4AB98s5xLIvgv0fNzGf_MuTpqtIBA" \
     'https://store.ishoptop.com/openapi/v1/products'
```

### 私有插件的Access-Token验证

私有插件使用基本的Access-Token验证，在晓拓管理后台应用市场内创建私有插件以获取Access-Token，然后使用调用API中提到的方式进行API调用。

### <span id="scopes">Scope列表</span>

- read_product, write_product
- read_order, write_order
- read_collection, write_collection
- read_shop
- read_comments, write_comments
- read_script_tags, write_script_tags
- read_customer, write_customer
- read_app_proxy, write_app_proxy
- read_price_rules, write_price_rules
- read_data, write_data

### <span id="hmac">Hmac算法</span>

```java
public static String HMACSHA256(String data, String key) throws Exception {
    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
    sha256_HMAC.init(secret_key);
    byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
    StringBuilder sb = new StringBuilder();
    for (byte item : array) {
        sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
    }
    return sb.toString().toUpperCase();
}
```

### Webhooks验签

每个webhook请求都会带有一个通过base64加密的**X-Shoptop-Hmac-Sha256**请求头。**X-Shoptop-Hmac-Sha256**的值是使用插件的API秘钥和请求中带有的参数生成的。如果需要验证请求是来自Shoptop，可以按照SDK中的算法计算出HMAC值，并和请求头中的**X-Shoptop-Hmac-Sha256**值进行比较，如果相同，则可以确认请求来自Shoptop。通过Hmac算法，使用请求中的数据和API secret key计算出hmac值，再经过Base64处理，即可得到HMAC值。

## SDK下载

### Shoptop-OpenAPI-SDK

Github下载地址：https://github.com/ishoptop/Shoptop-OpenAPI-SDK.git

### Shoptop-Webhooks-SDK

Github下载地址：https://github.com/ishoptop/Shoptop-Webhook-SDK.git





# Shoptop-OpenAPI V1



## 订单API

### 订单

#### 订单列表
**URL:** /openapi/v1/orders/

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 订单列表

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
pageNo|number|页码，从1开始|true|v1
pageSize|number|每页显示条数 default: 10, maximum: 200|true|v1
ids|string|id1, id2	订单id串, 用英文逗号分隔|false|v1
status|string|订单状态 opened(未完成)，placed(进行中)，finished(已完成)，cancelled(已取消)|false|v1
financialStatus|string|订单支付状态 waiting(待支付)，paying(支付中)，paid(已支付)，cancelled(已取消)，failed(失败)，refunding(退款中)，refund_failed(退款失败)，refunded(已退款), partially_refunded(部分退款)|false|v1
fulfillmentStatus|string|订单物流状态 initialled(空)，waiting(待发货)，partially_shipped(部分发货)，shipped(已发货)，partially_finished(部分完成)，finished(已完成), cancelled(取消)|false|v1
timeType|string|时间类型 created(创建时间)，updated(修改时间)，placed(订单支付时间)|false|v1
startTime|string|起始时间 （yyyy-MM-dd HH:mm:ss）|false|v1
endTime|string|结束时间（yyyy-MM-dd HH:mm:ss）|false|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/orders/?ids=qtz26v&financialStatus=osawf4&fulfillmentStatus=a5untx&timeType=bi4myb&endTime=2021-08-12 09:29:37&status=yyz5tj&pageSize=10&startTime=2021-08-12 09:29:37&pageNo=546
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|array|data|v1
└─orderNo|string|订单编号|v1
└─id|number|id|v1
└─totalPrice|string|总价|v1
└─subTotal|string|小计金额|v1
└─currency|string|货币类型|v1
└─financialStatus|string|订单支付状态waiting(待支付)，paying(支付中)，paid(已支付)，cancelled(已取消)，failed(失败)，refunding(退款中)，refund_failed(退款失败)，refunded(已退款)，partially_refunded(部分退款)|v1
└─orderStatus|string|订单状态 opened(未完成)，placed(进行中)，finished(已完成)，cancelled(已取消)|v1
└─canceledAt|string|订单取消时间|v1
└─cancelReason|string|订单取消原因|v1
└─orderNote|string|订单备注|v1
└─fulfillmentStatus|string|物流状态 initialled(空)，waiting(待发货)，partially_shipped(部分发货)，shipped(已发货)，partially_finished(部分完成)，finished(已完成), cancelled(取消)，returning(退货中)，returned(已退货)|v1,v1
└─customerDeletedAt|string|客户删除订单时间|v1
└─placedAt|string|订单确认时间|v1,v1
└─tags|string|订单标签|v1
└─discountCode|string|订单优惠码|v1
└─codeDiscountTotal|string|订单优惠码优惠价格|v1,v1
└─lineItemDiscountTotal|string|商品折扣|v1
└─customerNote|string|客户备注|v1
└─totalDiscount|string|订单折扣|v1
└─totalTax|string|总税费|v1
└─totalShipping|string|运费|v1
└─createdAt|string|创建时间|v1
└─updatedAt|string|更新时间|v1
└─lineItems|array|订单商品|v1,v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─paymentLine|object|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentChannel|string|支付渠道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentMethod|string|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─transactionNo|string|交易号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantId|string|收款账户ID|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantEmail|string|收款账户Email|v1
└─shippingLine|object|运费方案|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|物流方案名称|v1
└─billingAddress|object|账单地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
└─shippingAddress|object|物流地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|收货人姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─extraInfo|string|特殊物流字段|v1
└─fulfillments|array|运单信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─orderId|number|订单id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─status|string|运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompany|string|物流公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingNumber|string|运单号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompanyCode|string|物流公司编号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lineItems|array|测试|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─customer|object|顾客信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|客户邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─ordersCount|string|客户下单数|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─totalSpent|string|顾客消费总额|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|客户手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": [
        {
            "orderNo": "SVZ6191264",
            "id": "1376883403227529217",
            "totalPrice": "10.00",
            "subTotal": "11.00",
            "currency": "USD",
            "financialStatus": "waiting",
            "orderStatus": "opened",
            "canceledAt": null,
            "cancelReason": null,
            "orderNote": null,
            "fulfillmentStatus": "initialled",
            "customerDeletedAt": null,
            "placedAt": null,
            "tags": null,
            "discountCode": "KR88M4D7",
            "codeDiscountTotal": "1.00",
            "lineItemDiscountTotal": null,
            "customerNote": "",
            "totalDiscount": "1.00",
            "totalTax": "0.00",
            "totalShipping": "0.00",
            "createdAt": "2021-03-30 21:05:59",
            "updatedAt": "2021-03-31 09:15:39",
            "lineItems": [
                {
                    "productTitle": "4444",
                    "variantTitle": "4444",
                    "quantity": 11,
                    "note": null,
                    "image": "https://cdn.shoptop.com/1357301923878989826.png",
                    "price": "1.00",
                    "compareAtPrice": "10.00",
                    "total": "11.00",
                    "sku": "111",
                    "weight": "1.00",
                    "weightUnit": "kg",
                    "vendor": null,
                    "properties": null,
                    "productUrl": null,
                    "productHandle": "4444",
                    "id": "1376883403403689986",
                    "productId": "1357302156562198530",
                    "variantId": "1357302156591558657",
                    "fulfillmentStatus": "waiting"
                }
            ],
            "paymentLine": {
                "paymentChannel": "ocean",
                "paymentMethod": "credit_card",
                "transactionNo": null,
                "merchantId": null,
                "merchantEmail": null
            },
            "shippingLine": {
                "name": "freeno"
            },
            "billingAddress": {
                "firstName": "",
                "lastName": "23 33",
                "address1": "323",
                "address2": "3242",
                "city": "3243242",
                "zip": "3333",
                "province": "Abyan",
                "country": "Yemen",
                "company": null,
                "latitude": null,
                "longitude": null,
                "name": "23 33",
                "countryCode": "YE",
                "provinceCode": "YE-AB",
                "phoneAreaCode": null,
                "email": "123@123.com",
                "area": null
            },
            "shippingAddress": {
                "firstName": "23 33",
                "lastName": "23 33",
                "address1": "323",
                "address2": "3242",
                "phone": "",
                "city": "3243242",
                "zip": "3333",
                "province": "Abyan",
                "country": "Yemen",
                "company": null,
                "latitude": null,
                "longitude": null,
                "name": "23 33 23 33",
                "countryCode": "YE",
                "provinceCode": "YE-AB",
                "phoneAreaCode": null,
                "email": "123@123.com",
                "area": null,
                "extraInfo": ""
            },
            "fulfillments": null,
            "customer": {
                "email": "1234@164.com",
                "firstName": "22423",
                "lastName": "23423",
                "ordersCount": "0",
                "totalSpent": "0.00",
                "phone": null,
                "createdAt": "2021-03-30 21:05:54",
                "updatedAt": null
            }
        }
    ]
}
```

#### 订单详情
**URL:** /openapi/v1/orders/{id}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 订单详情

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|订单id|true|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/orders/254
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─orderNo|string|订单编号|v1
└─id|number|id|v1
└─totalPrice|string|总价|v1
└─subTotal|string|小计金额|v1
└─currency|string|货币类型|v1
└─financialStatus|string|订单支付状态waiting(待支付)，paying(支付中)，paid(已支付)，cancelled(已取消)，failed(失败)，refunding(退款中)，refund_failed(退款失败)，refunded(已退款)，partially_refunded(部分退款)|v1
└─orderStatus|string|订单状态 opened(未完成)，placed(进行中)，finished(已完成)，cancelled(已取消)|v1
└─canceledAt|string|订单取消时间|v1
└─cancelReason|string|订单取消原因|v1
└─orderNote|string|订单备注|v1
└─fulfillmentStatus|string|物流状态 initialled(空)，waiting(待发货)，partially_shipped(部分发货)，shipped(已发货)，partially_finished(部分完成)，finished(已完成), cancelled(取消)，returning(退货中)，returned(已退货)|v1,v1
└─customerDeletedAt|string|客户删除订单时间|v1
└─placedAt|string|订单确认时间|v1,v1
└─tags|string|订单标签|v1
└─discountCode|string|订单优惠码|v1
└─codeDiscountTotal|string|订单优惠码优惠价格|v1,v1
└─lineItemDiscountTotal|string|商品折扣|v1
└─customerNote|string|客户备注|v1
└─totalDiscount|string|订单折扣|v1
└─totalTax|string|总税费|v1
└─totalShipping|string|运费|v1
└─createdAt|string|创建时间|v1
└─updatedAt|string|更新时间|v1
└─lineItems|array|订单商品|v1,v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─paymentLine|object|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentChannel|string|支付渠道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentMethod|string|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─transactionNo|string|交易号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantId|string|收款账户ID|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantEmail|string|收款账户Email|v1
└─shippingLine|object|运费方案|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|物流方案名称|v1
└─billingAddress|object|账单地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
└─shippingAddress|object|物流地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|收货人姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─extraInfo|string|特殊物流字段|v1
└─fulfillments|array|运单信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─orderId|number|订单id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─status|string|运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompany|string|物流公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingNumber|string|运单号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompanyCode|string|物流公司编号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lineItems|array|测试|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─customer|object|顾客信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|客户邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─ordersCount|string|客户下单数|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─totalSpent|string|顾客消费总额|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|客户手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "orderNo": "SVZ6191264",
        "id": "1376883403227529217",
        "totalPrice": "10.00",
        "subTotal": "11.00",
        "currency": "USD",
        "financialStatus": "waiting",
        "orderStatus": "opened",
        "canceledAt": null,
        "cancelReason": null,
        "orderNote": null,
        "fulfillmentStatus": "initialled",
        "customerDeletedAt": null,
        "placedAt": null,
        "tags": null,
        "discountCode": "KR88M4D7",
        "codeDiscountTotal": "1.00",
        "lineItemDiscountTotal": null,
        "customerNote": "",
        "totalDiscount": "1.00",
        "totalTax": "0.00",
        "totalShipping": "0.00",
        "createdAt": "2021-03-30 21:05:59",
        "updatedAt": "2021-03-31 09:15:39",
        "lineItems": [
            {
                "productTitle": "4444",
                "variantTitle": "4444",
                "quantity": 11,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "11.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "4444",
                "id": "1376883403403689986",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "waiting"
            }
        ],
        "paymentLine": {
            "paymentChannel": "ocean",
            "paymentMethod": "credit_card",
            "transactionNo": null,
            "merchantId": null,
            "merchantEmail": null
        },
        "shippingLine": {
            "name": "freeno"
        },
        "billingAddress": {
            "firstName": "",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null
        },
        "shippingAddress": {
            "firstName": "23 33",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "phone": "",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33 23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null,
            "extraInfo": ""
        },
        "fulfillments": null,
        "customer": {
            "email": "1234@164.com",
            "firstName": "22423",
            "lastName": "23423",
            "ordersCount": "0",
            "totalSpent": "0.00",
            "phone": null,
            "createdAt": "2021-03-30 21:05:54",
            "updatedAt": null
        }
    }
}
```

#### 订单数量
**URL:** /openapi/v1/orders/count

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 订单数量

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
status|string|订单状态 opened(未完成)，placed(进行中)，finished(已完成)，cancelled(已取消)|false|v1
financialStatus|string|订单支付状态 waiting(待支付)，paying(支付中)，paid(已支付)，cancelled(已取消)，failed(失败)，refunding(退款中)，refund_failed(退款失败)，refunded(已退款), partially_refunded(部分退款)|false|v1
fulfillmentStatus|string|订单物流状态 initialled(空)，waiting(待发货)，partially_shipped(部分发货)，shipped(已发货)，partially_finished(部分完成)，finished(已完成), cancelled(取消)|false|v1
timeType|string|时间类型 created(创建时间)，updated(修改时间)|false|v1
startTime|string|起始时间（yyyy-MM-dd HH:mm:ss）|false|v1
endTime|string|结束时间（yyyy-MM-dd HH:mm:ss）|false|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/orders/count?timeType=x4cpcv&financialStatus=sul8my&endTime=2021-08-12 09:29:37&fulfillmentStatus=aevb0j&status=ikjfdb&startTime=2021-08-12 09:29:37
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─count|number|数量|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "count": "6"
    }
}
```

#### 取消订单
**URL:** /openapi/v1/orders/{id}/cancel

**Type:** POST


**Content-Type:** application/json; charset=utf-8

**Description:** 取消订单

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|订单id|true|v1

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
reason|string|取消原因|false|v1

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/orders/264/cancel --data '{
	"reason": "6k7d95"
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─orderNo|string|订单编号|v1
└─id|number|id|v1
└─totalPrice|string|总价|v1
└─subTotal|string|小计金额|v1
└─currency|string|货币类型|v1
└─financialStatus|string|订单支付状态waiting(待支付)，paying(支付中)，paid(已支付)，cancelled(已取消)，failed(失败)，refunding(退款中)，refund_failed(退款失败)，refunded(已退款)，partially_refunded(部分退款)|v1
└─orderStatus|string|订单状态 opened(未完成)，placed(进行中)，finished(已完成)，cancelled(已取消)|v1
└─canceledAt|string|订单取消时间|v1
└─cancelReason|string|订单取消原因|v1
└─orderNote|string|订单备注|v1
└─fulfillmentStatus|string|物流状态 initialled(空)，waiting(待发货)，partially_shipped(部分发货)，shipped(已发货)，partially_finished(部分完成)，finished(已完成), cancelled(取消)，returning(退货中)，returned(已退货)|v1,v1
└─customerDeletedAt|string|客户删除订单时间|v1
└─placedAt|string|订单确认时间|v1,v1
└─tags|string|订单标签|v1
└─discountCode|string|订单优惠码|v1
└─codeDiscountTotal|string|订单优惠码优惠价格|v1,v1
└─lineItemDiscountTotal|string|商品折扣|v1
└─customerNote|string|客户备注|v1
└─totalDiscount|string|订单折扣|v1
└─totalTax|string|总税费|v1
└─totalShipping|string|运费|v1
└─createdAt|string|创建时间|v1
└─updatedAt|string|更新时间|v1
└─lineItems|array|订单商品|v1,v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─paymentLine|object|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentChannel|string|支付渠道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentMethod|string|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─transactionNo|string|交易号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantId|string|收款账户ID|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantEmail|string|收款账户Email|v1
└─shippingLine|object|运费方案|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|物流方案名称|v1
└─billingAddress|object|账单地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
└─shippingAddress|object|物流地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|收货人姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─extraInfo|string|特殊物流字段|v1
└─fulfillments|array|运单信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─orderId|number|订单id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─status|string|运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompany|string|物流公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingNumber|string|运单号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompanyCode|string|物流公司编号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lineItems|array|测试|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─customer|object|顾客信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|客户邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─ordersCount|string|客户下单数|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─totalSpent|string|顾客消费总额|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|客户手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "orderNo": "SVZ6191264",
        "id": "1376883403227529217",
        "totalPrice": "10.00",
        "subTotal": "11.00",
        "currency": "USD",
        "financialStatus": "waiting",
        "orderStatus": "opened",
        "canceledAt": null,
        "cancelReason": null,
        "orderNote": null,
        "fulfillmentStatus": "initialled",
        "customerDeletedAt": null,
        "placedAt": null,
        "tags": null,
        "discountCode": "KR88M4D7",
        "codeDiscountTotal": "1.00",
        "lineItemDiscountTotal": null,
        "customerNote": "",
        "totalDiscount": "1.00",
        "totalTax": "0.00",
        "totalShipping": "0.00",
        "createdAt": "2021-03-30 21:05:59",
        "updatedAt": "2021-03-31 09:15:39",
        "lineItems": [
            {
                "productTitle": "4444",
                "variantTitle": "4444",
                "quantity": 11,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "11.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "4444",
                "id": "1376883403403689986",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "waiting"
            }
        ],
        "paymentLine": {
            "paymentChannel": "ocean",
            "paymentMethod": "credit_card",
            "transactionNo": null,
            "merchantId": null,
            "merchantEmail": null
        },
        "shippingLine": {
            "name": "freeno"
        },
        "billingAddress": {
            "firstName": "",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null
        },
        "shippingAddress": {
            "firstName": "23 33",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "phone": "",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33 23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null,
            "extraInfo": ""
        },
        "fulfillments": null,
        "customer": {
            "email": "1234@164.com",
            "firstName": "22423",
            "lastName": "23423",
            "ordersCount": "0",
            "totalSpent": "0.00",
            "phone": null,
            "createdAt": "2021-03-30 21:05:54",
            "updatedAt": null
        }
    }
}
```

#### 订单退款
**URL:** /openapi/v1/orders/{id}/refund

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 订单退款

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|订单id|true|v1

**Request-example:**
```
curl -X POST -i /openapi/v1/orders/328/refund
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1

**Response-example:**
```json
{
	"code": 201,
	"errorCode": 688,
	"msg": "请求成功",
	"data": true
}
```

#### 更新订单
**URL:** /openapi/v1/orders/{id}

**Type:** PUT


**Content-Type:** application/json; charset=utf-8

**Description:** 更新订单

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|订单id|true|v1

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
note|string|订单备注|false|v1
tags|string|订单标签, 英文逗号分隔|false|v1
shippingAddress|object|物流地址|false|v1
└─firstName|string|名|false|v1
└─lastName|string|姓|false|v1
└─email|string|邮箱|false|v1
└─phone|string|电话|false|v1
└─country|string|国家|false|v1
└─countryCode|string|国家编号|false|v1
└─province|string|省份|false|v1
└─provinceCode|string|省份编号|false|v1
└─area|string|区域|false|v1
└─city|string|城市|false|v1
└─address1|string|街道|false|v1
└─address2|string|寓所|false|v1
└─company|string|公司|false|v1
└─latitude|string|纬度|false|v1
└─longitude|string|经度|false|v1
└─phoneAreaCode|string|手机区号|false|v1
└─zip|string|邮编|false|v1

**Request-example:**
```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/orders/397 --data '{
	"note": "812yhn",
	"tags": "87wycy",
	"shippingAddress":  {
            "firstName": "23 33",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "phone": "",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33 23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null,
            "extraInfo": ""
   }
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─orderNo|string|订单编号|v1
└─id|number|id|v1
└─totalPrice|string|总价|v1
└─subTotal|string|小计金额|v1
└─currency|string|货币类型|v1
└─financialStatus|string|订单支付状态waiting(待支付)，paying(支付中)，paid(已支付)，cancelled(已取消)，failed(失败)，refunding(退款中)，refund_failed(退款失败)，refunded(已退款)，partially_refunded(部分退款)|v1
└─orderStatus|string|订单状态 opened(未完成)，placed(进行中)，finished(已完成)，cancelled(已取消)|v1
└─canceledAt|string|订单取消时间|v1
└─cancelReason|string|订单取消原因|v1
└─orderNote|string|订单备注|v1
└─fulfillmentStatus|string|物流状态 initialled(空)，waiting(待发货)，partially_shipped(部分发货)，shipped(已发货)，partially_finished(部分完成)，finished(已完成), cancelled(取消)，returning(退货中)，returned(已退货)|v1
└─customerDeletedAt|string|客户删除订单时间|v1
└─placedAt|string|订单确认时间|v1
└─tags|string|订单标签|v1
└─discountCode|string|订单优惠码|v1
└─codeDiscountTotal|string|订单优惠码优惠价格|v1
└─lineItemDiscountTotal|string|商品折扣|v1
└─customerNote|string|客户备注|v1
└─totalDiscount|string|订单折扣|v1
└─totalTax|string|总税费|v1
└─totalShipping|string|运费|v1
└─createdAt|string|创建时间|v1
└─updatedAt|string|更新时间|v1
└─lineItems|array|订单商品|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─paymentLine|object|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentChannel|string|支付渠道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─paymentMethod|string|支付方式|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─transactionNo|string|交易号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantId|string|收款账户ID|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─merchantEmail|string|收款账户Email|v1
└─shippingLine|object|运费方案|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|物流方案名称|v1
└─billingAddress|object|账单地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
└─shippingAddress|object|物流地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|收货人名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|收货人姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address1|string|街道|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address2|string|寓所|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─city|string|城市|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─zip|string|邮编|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─province|string|省份|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─country|string|国家|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─company|string|公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─latitude|string|纬度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─longitude|string|经度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─name|string|收货人姓名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─countryCode|string|国家代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─provinceCode|string|省份代码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phoneAreaCode|string|手机区号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─area|string|区域|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─extraInfo|string|特殊物流字段|v1
└─fulfillments|array|运单信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─orderId|number|订单id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─status|string|运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompany|string|物流公司|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingNumber|string|运单号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─trackingCompanyCode|string|物流公司编号|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lineItems|array|测试|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle|string|商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle|string|子商品标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity|number|商品数量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image|string|商品图片|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|string|商品售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|string|商品原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total|string|总价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|string|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位(kg, g ...)|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor|string|商品供应商|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties|string|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl|string|商品url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle|string|商品handle|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId|string|商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId|string|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus|string|运单状态|v1
└─customer|object|顾客信息|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─email|string|客户邮箱|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─firstName|string|名|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─lastName|string|姓|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─ordersCount|string|客户下单数|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─totalSpent|string|顾客消费总额|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─phone|string|客户手机号码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createdAt|string|创建时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updatedAt|string|修改时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "orderNo": "SVZ6191264",
        "id": "1376883403227529217",
        "totalPrice": "10.00",
        "subTotal": "11.00",
        "currency": "USD",
        "financialStatus": "waiting",
        "orderStatus": "opened",
        "canceledAt": null,
        "cancelReason": null,
        "orderNote": null,
        "fulfillmentStatus": "initialled",
        "customerDeletedAt": null,
        "placedAt": null,
        "tags": null,
        "discountCode": "KR88M4D7",
        "codeDiscountTotal": "1.00",
        "lineItemDiscountTotal": null,
        "customerNote": "",
        "totalDiscount": "1.00",
        "totalTax": "0.00",
        "totalShipping": "0.00",
        "createdAt": "2021-03-30 21:05:59",
        "updatedAt": "2021-03-31 09:15:39",
        "lineItems": [
            {
                "productTitle": "4444",
                "variantTitle": "4444",
                "quantity": 11,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "11.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "4444",
                "id": "1376883403403689986",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "waiting"
            }
        ],
        "paymentLine": {
            "paymentChannel": "ocean",
            "paymentMethod": "credit_card",
            "transactionNo": null,
            "merchantId": null,
            "merchantEmail": null
        },
        "shippingLine": {
            "name": "freeno"
        },
        "billingAddress": {
            "firstName": "",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null
        },
        "shippingAddress": {
            "firstName": "23 33",
            "lastName": "23 33",
            "address1": "323",
            "address2": "3242",
            "phone": "",
            "city": "3243242",
            "zip": "3333",
            "province": "Abyan",
            "country": "Yemen",
            "company": null,
            "latitude": null,
            "longitude": null,
            "name": "23 33 23 33",
            "countryCode": "YE",
            "provinceCode": "YE-AB",
            "phoneAreaCode": null,
            "email": "123@123.com",
            "area": null,
            "extraInfo": ""
        },
        "fulfillments": null,
        "customer": {
            "email": "1234@164.com",
            "firstName": "22423",
            "lastName": "23423",
            "ordersCount": "0",
            "totalSpent": "0.00",
            "phone": null,
            "createdAt": "2021-03-30 21:05:54",
            "updatedAt": null
        }
    }
}
```

### 运单

#### 运单列表

**URL:** /openapi/v1/orders/{orderId}/fulfillments

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 运单列表

**Path-parameters:**

| Parameter | Type  | Description | Required | Since |
| --------- | ----- | ----------- | -------- | ----- |
| orderId   | number | 订单id      | true     | v1    |

**Query-parameters:**

| Parameter | Type   | Description                                   | Required | Since |
| --------- | ------ | --------------------------------------------- | -------- | ----- |
| pageNo    | number  | 页码，从1开始                                 | true     | v1    |
| pageSize  | number  | 每页显示条数 default: 10, maximum: 200        | true     | v1    |
| timeType  | string | 时间类型 created(创建时间)，updated(修改时间) | false    | v1    |
| startTime | string | 起始时间（yyyy-MM-dd HH:mm:ss）               | false    | v1    |
| endTime   | string | 结束时间（yyyy-MM-dd HH:mm:ss）               | false    | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/orders/439/fulfillments?startTime=2021-08-12 09:29:37&pageSize=10&timeType=hyb7os&endTime=2021-08-12 09:29:37&pageNo=334
```

**Response-fields:**

| Field                                             | Type   | Description                                                  | Since |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ | ----- |
| code                                              | number  | code                                                         | v1    |
| errorCode                                         | number  | errorCode                                                    | v1    |
| msg                                               | string | message                                                      | v1    |
| data                                              | array  | data                                                         | v1    |
| └─id                                              | number  | id                                                           | v1    |
| └─orderId                                         | number  | 订单id                                                       | v1    |
| └─status                                          | string | 运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消) | v1    |
| └─createdAt                                       | string | 创建时间                                                     | v1    |
| └─updatedAt                                       | string | 修改时间                                                     | v1    |
| └─trackingCompany                                 | string | 物流公司                                                     | v1    |
| └─trackingNumber                                  | string | 运单号                                                       | v1    |
| └─trackingCompanyCode                             | string | 物流公司编号                                                 | v1    |
| └─lineItems                                       | array  | 测试                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle      | string | 商品标题                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle      | string | 子商品标题                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity          | number  | 商品数量                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note              | string | 备注                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image             | string | 商品图片                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price             | string | 商品售价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice    | string | 商品原价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total             | string | 总价                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku               | string | sku                                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight            | string | 重量                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit        | string | 重量单位(kg, g ...)                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor            | string | 商品供应商                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties        | string | 商品属性                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl        | string | 商品url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle     | string | 商品handle                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id                | number  | id                                                           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId         | string | 商品id                                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId         | string | 子商品id                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus | string | 运单状态                                                     | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": [
        {
            "id": "1379403573553049602",
            "orderId": "1379399717192511490",
            "status": "finished",
            "createdAt": "2021-04-06 20:00:15",
            "updatedAt": "2021-04-06 20:00:24",
            "trackingCompany": "YD2021",
            "trackingNumber": "韵达国际",
            "trackingCompanyCode": "-1",
            "lineItems": [
                {
                    "productTitle": "测试二",
                    "variantTitle": "测试二",
                    "quantity": 1,
                    "note": null,
                    "image": "https://cdn.shoptop.com/1356392122307657729.png",
                    "price": "45.00",
                    "compareAtPrice": "90.00",
                    "total": "45.00",
                    "sku": "20201908",
                    "weight": "5.00",
                    "weightUnit": "kg",
                    "vendor": "这是测试供应商",
                    "properties": null,
                    "productUrl": null,
                    "productHandle": "测试二",
                    "id": "1379399717318340610",
                    "productId": "1379399645092483073",
                    "variantId": "1379399645126037505",
                    "fulfillmentStatus": "finished"
                }
            ]
        }
    ]
}
```

#### 运单数量

**URL:** /openapi/v1/orders/{orderId}/fulfillments/count

**Type:** GET


**Content-Type:** application/json; charset=utf-8

**Description:** 运单数量

**Path-parameters:**

| Parameter | Type  | Description | Required | Since |
| --------- | ----- | ----------- | -------- | ----- |
| orderId   | number | 订单id      | true     | v1    |

**Body-parameters:**

| Parameter | Type   | Description                                   | Required | Since |
| --------- | ------ | --------------------------------------------- | -------- | ----- |
| timeType  | string | 时间类型 created(创建时间)，updated(修改时间) | false    | v1    |
| startTime | string | 起始时间（yyyy-MM-dd HH:mm:ss）               | false    | v1    |
| endTime   | string | 结束时间（yyyy-MM-dd HH:mm:ss）               | false    | v1    |

**Request-example:**

```
curl -X GET -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/orders/235/fulfillments/count --data '{
	"timeType": "f0uhbk",
	"startTime": "2021-08-12 09:29:37",
	"endTime": "2021-08-12 09:29:37"
}'
```

**Response-fields:**

| Field     | Type   | Description | Since |
| --------- | ------ | ----------- | ----- |
| code      | number  | code        | v1    |
| errorCode | number  | errorCode   | v1    |
| msg       | string | message     | v1    |
| data      | object | data        | v1    |
| └─count   | number  | 数量        | v1    |

**Response-example:**

```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"count": 131
	}
}
```

#### 运单详情

**URL:** /openapi/v1/orders/{orderId}/fulfillments/{fulfillmentId}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 运单详情

**Path-parameters:**

| Parameter     | Type  | Description | Required | Since |
| ------------- | ----- | ----------- | -------- | ----- |
| orderId       | number | 订单id      | true     | v1    |
| fulfillmentId | number | 运单id      | true     | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/orders/430/fulfillments/904
```

**Response-fields:**

| Field                                             | Type   | Description                                                  | Since |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ | ----- |
| code                                              | number  | code                                                         | v1    |
| errorCode                                         | number  | errorCode                                                    | v1    |
| msg                                               | string | message                                                      | v1    |
| data                                              | object | data                                                         | v1    |
| └─id                                              | number  | id                                                           | v1    |
| └─orderId                                         | number  | 订单id                                                       | v1    |
| └─status                                          | string | 运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消) | v1    |
| └─createdAt                                       | string | 创建时间                                                     | v1    |
| └─updatedAt                                       | string | 修改时间                                                     | v1    |
| └─trackingCompany                                 | string | 物流公司                                                     | v1    |
| └─trackingNumber                                  | string | 运单号                                                       | v1    |
| └─trackingCompanyCode                             | string | 物流公司编号                                                 | v1    |
| └─lineItems                                       | array  | 测试                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle      | string | 商品标题                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle      | string | 子商品标题                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity          | number  | 商品数量                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note              | string | 备注                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image             | string | 商品图片                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price             | string | 商品售价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice    | string | 商品原价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total             | string | 总价                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku               | string | sku                                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight            | string | 重量                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit        | string | 重量单位(kg, g ...)                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor            | string | 商品供应商                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties        | string | 商品属性                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl        | string | 商品url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle     | string | 商品handle                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id                | number  | id                                                           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId         | string | 商品id                                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId         | string | 子商品id                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus | string | 运单状态                                                     | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425658127726391298",
        "orderId": "1425657742634758146",
        "status": "shipped",
        "createdAt": "2021-08-12 11:19:20",
        "updatedAt": "2021-08-12 11:19:20",
        "trackingCompany": "安得物流",
        "trackingNumber": "2113",
        "trackingCompanyCode": "annto",
        "lineItems": [
            {
                "productTitle": "测试-444400",
                "variantTitle": "测试-444400",
                "quantity": 1,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "1.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "测试-4444",
                "id": "1425657744870322178",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "shipped"
            }
        ]
    }
}
```

#### 创建运单

**URL:** /openapi/v1/orders/{orderId}/fulfillments

**Type:** POST


**Content-Type:** application/json; charset=utf-8

**Description:** 创建运单

**Path-parameters:**

| Parameter | Type  | Description | Required | Since |
| --------- | ----- | ----------- | -------- | ----- |
| orderId   | number | 订单id      | true     | v1    |

**Body-parameters:**

| Parameter           | Type   | Description                    | Required | Since |
| ------------------- | ------ | ------------------------------ | -------- | ----- |
| lineItemIds         | string | id1,id2 运单包含的line Item Id | true     | v1    |
| trackingNumber      | string | 运单号                         | true     | v1    |
| trackingCompany     | string | 物流公司名称                   | true     | v1    |
| trackingCompanyCode | string | 物流公司代码                   | true     | v1    |

**Request-example:**

```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/orders/768/fulfillments --data '{
	"lineItemIds": "wyn48o",
	"trackingNumber": "tpg0bm",
	"trackingCompany": "Shoptop",
	"trackingCompanyCode": "79665"
}'
```

**Response-fields:**

| Field                                             | Type   | Description                                                  | Since |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ | ----- |
| code                                              | number  | code                                                         | v1    |
| errorCode                                         | number  | errorCode                                                    | v1    |
| msg                                               | string | message                                                      | v1    |
| data                                              | object | data                                                         | v1    |
| └─id                                              | number  | id                                                           | v1    |
| └─orderId                                         | number  | 订单id                                                       | v1    |
| └─status                                          | string | 运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消) | v1    |
| └─createdAt                                       | string | 创建时间                                                     | v1    |
| └─updatedAt                                       | string | 修改时间                                                     | v1    |
| └─trackingCompany                                 | string | 物流公司                                                     | v1    |
| └─trackingNumber                                  | string | 运单号                                                       | v1    |
| └─trackingCompanyCode                             | string | 物流公司编号                                                 | v1    |
| └─lineItems                                       | array  | 测试                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle      | string | 商品标题                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle      | string | 子商品标题                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity          | number  | 商品数量                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note              | string | 备注                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image             | string | 商品图片                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price             | string | 商品售价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice    | string | 商品原价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total             | string | 总价                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku               | string | sku                                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight            | string | 重量                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit        | string | 重量单位(kg, g ...)                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor            | string | 商品供应商                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties        | string | 商品属性                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl        | string | 商品url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle     | string | 商品handle                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id                | number  | id                                                           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId         | string | 商品id                                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId         | string | 子商品id                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus | string | 运单状态                                                     | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1379403573553049602",
        "orderId": "1379399717192511490",
        "status": "finished",
        "createdAt": "2021-04-06 20:00:15",
        "updatedAt": "2021-04-06 20:00:24",
        "trackingCompany": "YD2021",
        "trackingNumber": "韵达国际",
        "trackingCompanyCode": "-1",
        "lineItems": [
            {
                "productTitle": "测试二",
                "variantTitle": "测试二",
                "quantity": 1,
                "note": null,
                "image": "https://cdn.shoptop.com/1356392122307657729.png",
                "price": "45.00",
                "compareAtPrice": "90.00",
                "total": "45.00",
                "sku": "20201908",
                "weight": "5.00",
                "weightUnit": "kg",
                "vendor": "这是测试供应商",
                "properties": null,
                "productUrl": null,
                "productHandle": "测试二",
                "id": "1379399717318340610",
                "productId": "1379399645092483073",
                "variantId": "1379399645126037505",
                "fulfillmentStatus": "finished"
            }
        ]
    }
}
```

#### 更新运单

**URL:** /openapi/v1/orders/{orderId}/fulfillments/{fulfillmentId}

**Type:** PUT

**Content-Type:** application/json; charset=utf-8

**Description:** 更新运单

**Path-parameters:**

| Parameter     | Type  | Description | Required | Since |
| ------------- | ----- | ----------- | -------- | ----- |
| orderId       | number | 订单id      | true     | v1    |
| fulfillmentId | number | 运单id      | true     | v1    |

**Body-parameters:**

| Parameter           | Type   | Description  | Required | Since |
| ------------------- | ------ | ------------ | -------- | ----- |
| trackingNumber      | string | 运单号       | true     | v1    |
| trackingCompany     | string | 物流公司名称 | true     | v1    |
| trackingCompanyCode | string | 物流公司代码 | true     | v1    |

**Request-example:**

```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/orders/250/fulfillments/704 --data '{
	"trackingNumber": "sokx0i",
	"trackingCompany": "Shoptop",
	"trackingCompanyCode": "79665"
}'
```

**Response-fields:**

| Field                                             | Type   | Description                                                  | Since |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ | ----- |
| code                                              | number  | code                                                         | v1    |
| errorCode                                         | number  | errorCode                                                    | v1    |
| msg                                               | string | message                                                      | v1    |
| data                                              | object | data                                                         | v1    |
| └─id                                              | number  | id                                                           | v1    |
| └─orderId                                         | number  | 订单id                                                       | v1    |
| └─status                                          | string | 运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消) | v1    |
| └─createdAt                                       | string | 创建时间                                                     | v1    |
| └─updatedAt                                       | string | 修改时间                                                     | v1    |
| └─trackingCompany                                 | string | 物流公司                                                     | v1    |
| └─trackingNumber                                  | string | 运单号                                                       | v1    |
| └─trackingCompanyCode                             | string | 物流公司编号                                                 | v1    |
| └─lineItems                                       | array  | 测试                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle      | string | 商品标题                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle      | string | 子商品标题                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity          | number  | 商品数量                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note              | string | 备注                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image             | string | 商品图片                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price             | string | 商品售价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice    | string | 商品原价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total             | string | 总价                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku               | string | sku                                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight            | string | 重量                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit        | string | 重量单位(kg, g ...)                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor            | string | 商品供应商                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties        | string | 商品属性                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl        | string | 商品url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle     | string | 商品handle                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id                | number  | id                                                           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId         | string | 商品id                                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId         | string | 子商品id                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus | string | 运单状态                                                     | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425658127726391298",
        "orderId": "1425657742634758146",
        "status": "shipped",
        "createdAt": "2021-08-12 11:19:20",
        "updatedAt": "2021-08-12 11:19:20",
        "trackingCompany": "安得物流",
        "trackingNumber": "2113",
        "trackingCompanyCode": "annto",
        "lineItems": [
            {
                "productTitle": "测试-444400",
                "variantTitle": "测试-444400",
                "quantity": 1,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "1.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "测试-4444",
                "id": "1425657744870322178",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "shipped"
            }
        ]
    }
}
```

#### 完成运单

**URL:** /openapi/v1/orders/{orderId}/fulfillments/{fulfillmentId}/complete

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 完成运单

**Path-parameters:**

| Parameter     | Type  | Description | Required | Since |
| ------------- | ----- | ----------- | -------- | ----- |
| orderId       | number | 订单id      | true     | v1    |
| fulfillmentId | number | 运单id      | true     | v1    |

**Request-example:**

```
curl -X POST -i /openapi/v1/orders/878/fulfillments/261/complete
```

**Response-fields:**

| Field                                             | Type   | Description                                                  | Since |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ | ----- |
| code                                              | number  | code                                                         | v1    |
| errorCode                                         | number  | errorCode                                                    | v1    |
| msg                                               | string | message                                                      | v1    |
| data                                              | object | data                                                         | v1    |
| └─id                                              | number  | id                                                           | v1    |
| └─orderId                                         | number  | 订单id                                                       | v1    |
| └─status                                          | string | 运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消) | v1    |
| └─createdAt                                       | string | 创建时间                                                     | v1    |
| └─updatedAt                                       | string | 修改时间                                                     | v1    |
| └─trackingCompany                                 | string | 物流公司                                                     | v1    |
| └─trackingNumber                                  | string | 运单号                                                       | v1    |
| └─trackingCompanyCode                             | string | 物流公司编号                                                 | v1    |
| └─lineItems                                       | array  | 测试                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle      | string | 商品标题                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle      | string | 子商品标题                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity          | number  | 商品数量                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note              | string | 备注                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image             | string | 商品图片                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price             | string | 商品售价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice    | string | 商品原价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total             | string | 总价                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku               | string | sku                                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight            | string | 重量                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit        | string | 重量单位(kg, g ...)                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor            | string | 商品供应商                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties        | string | 商品属性                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl        | string | 商品url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle     | string | 商品handle                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id                | number  | id                                                           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId         | string | 商品id                                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId         | string | 子商品id                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus | string | 运单状态                                                     | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425658127726391298",
        "orderId": "1425657742634758146",
        "status": "shipped",
        "createdAt": "2021-08-12 11:19:20",
        "updatedAt": "2021-08-12 11:19:20",
        "trackingCompany": "安得物流",
        "trackingNumber": "2113",
        "trackingCompanyCode": "annto",
        "lineItems": [
            {
                "productTitle": "测试-444400",
                "variantTitle": "测试-444400",
                "quantity": 1,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "1.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "测试-4444",
                "id": "1425657744870322178",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "shipped"
            }
        ]
    }
}
```

#### 取消运单

**URL:** /openapi/v1/orders/{orderId}/fulfillments/{fulfillmentId}/cancel

**Type:** POST


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 取消运单

**Path-parameters:**

| Parameter     | Type  | Description | Required | Since |
| ------------- | ----- | ----------- | -------- | ----- |
| orderId       | number | 订单id      | true     | v1    |
| fulfillmentId | number | 运单id      | true     | v1    |

**Request-example:**

```
curl -X POST -i /openapi/v1/orders/63/fulfillments/442/cancel
```

**Response-fields:**

| Field                                             | Type   | Description                                                  | Since |
| ------------------------------------------------- | ------ | ------------------------------------------------------------ | ----- |
| code                                              | number  | code                                                         | v1    |
| errorCode                                         | number  | errorCode                                                    | v1    |
| msg                                               | string | message                                                      | v1    |
| data                                              | object | data                                                         | v1    |
| └─id                                              | number  | id                                                           | v1    |
| └─orderId                                         | number  | 订单id                                                       | v1    |
| └─status                                          | string | 运单状态 waiting(待发货),shipped(已发货),finished(已完成),cancelled(已取消) | v1    |
| └─createdAt                                       | string | 创建时间                                                     | v1    |
| └─updatedAt                                       | string | 修改时间                                                     | v1    |
| └─trackingCompany                                 | string | 物流公司                                                     | v1    |
| └─trackingNumber                                  | string | 运单号                                                       | v1    |
| └─trackingCompanyCode                             | string | 物流公司编号                                                 | v1    |
| └─lineItems                                       | array  | 测试                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productTitle      | string | 商品标题                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantTitle      | string | 子商品标题                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─quantity          | number  | 商品数量                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─note              | string | 备注                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─image             | string | 商品图片                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price             | string | 商品售价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice    | string | 商品原价                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─total             | string | 总价                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku               | string | sku                                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight            | string | 重量                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit        | string | 重量单位(kg, g ...)                                          | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─vendor            | string | 商品供应商                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─properties        | string | 商品属性                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productUrl        | string | 商品url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productHandle     | string | 商品handle                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id                | number  | id                                                           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─productId         | string | 商品id                                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─variantId         | string | 子商品id                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fulfillmentStatus | string | 运单状态                                                     | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425658127726391298",
        "orderId": "1425657742634758146",
        "status": "shipped",
        "createdAt": "2021-08-12 11:19:20",
        "updatedAt": "2021-08-12 11:19:20",
        "trackingCompany": "安得物流",
        "trackingNumber": "2113",
        "trackingCompanyCode": "annto",
        "lineItems": [
            {
                "productTitle": "测试-444400",
                "variantTitle": "测试-444400",
                "quantity": 1,
                "note": null,
                "image": "https://cdn.shoptop.com/1357301923878989826.png",
                "price": "1.00",
                "compareAtPrice": "10.00",
                "total": "1.00",
                "sku": "111",
                "weight": "1.00",
                "weightUnit": "kg",
                "vendor": null,
                "properties": null,
                "productUrl": null,
                "productHandle": "测试-4444",
                "id": "1425657744870322178",
                "productId": "1357302156562198530",
                "variantId": "1357302156591558657",
                "fulfillmentStatus": "shipped"
            }
        ]
    }
}
```



## 商品API

### 商品

#### 商品列表

**URL:** /openapi/v1/products/

**Type:** GET

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 商品列表

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
pageNo|number|页码，从1开始|true|v1
pageSize|number|每页显示条数 default: 10, maximum: 200|true|v1
published|number|商品是否已发布,0:未发布,1:已发布,2:全部|false|v1
keyword|string|搜索关键词|false|v1
shopId|number|店铺id|false|v1
beginCreateTime|string|创建时间开始时间|false|v1
endCreateTime|string|创建时间结束时间|false|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/products/?endCreateTime=2021-08-12 09:29:38&published=144&shopId=497&pageSize=10&pageNo=856&keyword=ezt65m&beginCreateTime=2021-08-12 09:29:38
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|array|data|v1
└─spuId|number|商品spuID|v1
└─seoTitle|string|seo标题|v1
└─seoKeywords|string|seo关键词|v1
└─seoDescription|string|seo描述|v1
└─handle|string|商品url尾缀|v1
└─goodsImage|object|商品主图|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId|number|图片文件id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path|string|图片位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|图片url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height|number|高度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width|number|宽度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|文件格式类型|v1
└─goodsTitle|string|商品标题|v1
└─goodsBrief|string|副标题|v1
└─spu|string|spu属性|v1
└─inventoryTracking|number|是否跟踪库存,1:跟踪,0:不跟踪|v1
└─inventoryPolicy|number|跟踪库存策略,1:库存为0时不允许购买,2:库存为0时允许购买,3:库存为0时自动下架|v1
└─needVariantImage|number|sku款式是否需要配图,0:不需要,1:需要|v1
└─published|number|是否上架,1:已上架,0:已下架|v1
└─publishedAt|string|上架时间|v1
└─requiresShipping|number|是否需要物流|v1
└─taxable|number|是否对此商品收税|v1
└─vendorName|string|供应商名称|v1
└─vendorUrl|string|供应商url|v1
└─amazonLink|string|亚马逊商品链接|v1
└─shopId|number|店铺id|v1
└─goodsDescription|string|商品描述|v1
└─isFreeShipping|number|是否免运费(0不免运费,1免运费)|v1
└─isSensitiveGoods|number|是否为敏感商品|v1
└─isSingleSku|number|是否单一款式|v1
└─inventoryQuantity|number|库存数量|v1
└─createTime|string|创建时间|v1
└─updateTime|string|修改时间|v1
└─collections|array|商品所属的专辑|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─collectionId|number|商品专辑id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─collectionTitle|string|商品专辑标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─smart|number|是否使用智能添加|v1
└─skus|array|子商品|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─skuId|number|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuId|number|店铺id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─barcode|string|条形码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─shopId|number|店铺id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|number|原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|number|售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─inventoryQuantity|number|库存|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─skuNote|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─skuImage|object|图片数据|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId|number|图片文件id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path|string|图片位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|图片url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height|number|高度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width|number|宽度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|文件格式类型|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─specOption1|string|规格属性1|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─specOption2|string|规格属性2|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─specOption3|string|规格属性3|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|number|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位|v1
└─images|array|商品图片列表|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─position|number|图片排序/位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─imageData|object|图片数据|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId|number|图片文件id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path|string|图片位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|图片url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height|number|高度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width|number|宽度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|文件格式类型|v1
└─specs|array|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuSpecName|string|规格名称|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─position|string|规格位置,1,2,3|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuSpecValues|array|商品规格属性|v1
└─goodsTags|array|商品标签|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": [
        {
            "spuId": "1425655086029709313",
            "seoTitle": "High-performance backpacks for backpacks, hiking, and camping",
            "seoKeywords": "",
            "seoDescription": "High-performance backpacks for backpacks, hiking, and camping",
            "handle": "High-performance-backpacks-for-backpacks-hiking-and-camping_1s25",
            "goodsImage": {
                "fileRepositoryId": "1357301923878989826",
                "path": "1357301923878989826.png",
                "url": "https://cdn.shoptop.com/1357301923878989826.png",
                "height": 239,
                "width": 378,
                "type": "image/png"
            },
            "goodsTitle": "High-performance backpacks for backpacks, hiking, and camping",
            "goodsBrief": "High-performance backpacks for backpacks, hiking, and camping",
            "spu": "",
            "inventoryTracking": 0,
            "inventoryPolicy": 2,
            "needVariantImage": 0,
            "published": 1,
            "publishedAt": null,
            "requiresShipping": 1,
            "taxable": 0,
            "vendorName": null,
            "vendorUrl": null,
            "amazonLink": null,
            "shopId": 100103,
            "goodsDescription": "<p class=\"p1\" .... bags for strategic packaging</p>",
            "isFreeShipping": 0,
            "isSensitiveGoods": 0,
            "isSingleSku": 1,
            "inventoryQuantity": 0,
            "createTime": "2021-08-12 11:07:15",
            "updateTime": "2021-08-12 11:07:15",
            "collections": null,
            "skus": [
                {
                    "skuId": "1425655086088429570",
                    "spuId": "1425655086029709313",
                    "barcode": "111",
                    "shopId": 100103,
                    "compareAtPrice": 10000,
                    "price": 10000,
                    "inventoryQuantity": 0,
                    "skuNote": null,
                    "sku": "11",
                    "skuImage": null,
                    "specOption1": null,
                    "specOption2": null,
                    "specOption3": null,
                    "weight": 1.00,
                    "weightUnit": "kg"
                }
            ],
            "images": [
                {
                    "position": 0,
                    "imageData": {
                        "fileRepositoryId": "1357301923878989826",
                        "path": "1357301923878989826.png",
                        "url": "https://cdn.shoptop.com/1357301923878989826.png",
                        "height": 239,
                        "width": 378,
                        "type": "image/png"
                    }
                }
            ],
            "specs": [],
            "goodsTags": []
        }
    ]
}
```

#### 商品数量
**URL:** /openapi/v1/products/count

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 商品数量

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
published|number|商品是否已发布,0:未发布,1:已发布,2:全部|false|v1
keyword|string|搜索关键词|false|v1
shopId|number|店铺id|false|v1
beginCreateTime|string|创建时间开始时间|false|v1
endCreateTime|string|创建时间结束时间|false|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/products/count?keyword=xb6f9y&shopId=205&endCreateTime=2021-08-12 09:29:38&published=220&beginCreateTime=2021-08-12 09:29:38
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─count|number|数量|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "count": "88"
    }
}
```

#### 商品详情
**URL:** /openapi/v1/products/{spuId}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 商品详情

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
spuId|number|商品id|true|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/products/2b3d272d-12b5-4133-bbb0-5e43e748c730
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─spuId|number|商品spuID|v1
└─seoTitle|string|seo标题|v1
└─seoKeywords|string|seo关键词|v1
└─seoDescription|string|seo描述|v1
└─handle|string|商品url尾缀|v1
└─goodsImage|object|商品主图|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId|number|图片文件id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path|string|图片位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|图片url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height|number|高度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width|number|宽度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|文件格式类型|v1
└─goodsTitle|string|商品标题|v1
└─goodsBrief|string|副标题|v1
└─spu|string|spu属性|v1
└─inventoryTracking|number|是否跟踪库存,1:跟踪,0:不跟踪|v1
└─inventoryPolicy|number|跟踪库存策略,1:库存为0时不允许购买,2:库存为0时允许购买,3:库存为0时自动下架|v1
└─needVariantImage|number|sku款式是否需要配图,0:不需要,1:需要|v1
└─published|number|是否上架,1:已上架,0:已下架|v1
└─publishedAt|string|上架时间|v1
└─requiresShipping|number|是否需要物流|v1
└─taxable|number|是否对此商品收税|v1
└─vendorName|string|供应商名称|v1
└─vendorUrl|string|供应商url|v1
└─amazonLink|string|亚马逊商品链接|v1
└─shopId|number|店铺id|v1
└─goodsDescription|string|商品描述|v1
└─isFreeShipping|number|是否免运费(0不免运费,1免运费)|v1
└─isSensitiveGoods|number|是否为敏感商品|v1
└─isSingleSku|number|是否单一款式|v1
└─inventoryQuantity|number|库存数量|v1
└─createTime|string|创建时间|v1
└─updateTime|string|修改时间|v1
└─collections|array|商品所属的专辑|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─collectionId|number|商品专辑id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─collectionTitle|string|商品专辑标题|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─smart|number|是否使用智能添加|v1
└─skus|array|子商品|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─skuId|number|子商品id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuId|number|店铺id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─barcode|string|条形码|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─shopId|number|店铺id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─compareAtPrice|number|原价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─price|number|售价|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─inventoryQuantity|number|库存|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─skuNote|string|备注|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sku|string|sku属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─skuImage|object|图片数据|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId|number|图片文件id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path|string|图片位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|图片url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height|number|高度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width|number|宽度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|文件格式类型|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─specOption1|string|规格属性1|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─specOption2|string|规格属性2|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─specOption3|string|规格属性3|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weight|number|重量|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─weightUnit|string|重量单位|v1
└─images|array|商品图片列表|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─position|number|图片排序/位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─imageData|object|图片数据|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId|number|图片文件id|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path|string|图片位置|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url|string|图片url|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height|number|高度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width|number|宽度|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type|string|文件格式类型|v1
└─specs|array|商品属性|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuSpecName|string|规格名称|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─position|string|规格位置,1,2,3|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuSpecValues|array|商品规格属性|v1
└─goodsTags|array|商品标签|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "spuId": "1425655086029709313",
        "seoTitle": "High-performance backpacks for backpacks, hiking, and camping",
        "seoKeywords": "",
        "seoDescription": "High-performance backpacks for backpacks, hiking, and camping",
        "handle": "High-performance-backpacks-for-backpacks-hiking-and-camping_1s25",
        "goodsImage": {
            "fileRepositoryId": "1357301923878989826",
            "path": "1357301923878989826.png",
            "url": "https://cdn.shoptop.com/1357301923878989826.png",
            "height": 239,
            "width": 378,
            "type": "image/png"
        },
        "goodsTitle": "High-performance backpacks for backpacks, hiking, and camping",
        "goodsBrief": "High-performance backpacks for backpacks, hiking, and camping",
        "spu": "",
        "inventoryTracking": 0,
        "inventoryPolicy": 2,
        "needVariantImage": 0,
        "published": 1,
        "publishedAt": null,
        "requiresShipping": 1,
        "taxable": 0,
        "vendorName": null,
        "vendorUrl": null,
        "amazonLink": null,
        "shopId": 100103,
        "goodsDescription": "<p class=\"p1\" style=\" ... for strategic packaging</p>",
        "isFreeShipping": 0,
        "isSensitiveGoods": 0,
        "isSingleSku": 1,
        "inventoryQuantity": 0,
        "createTime": "2021-08-12 11:07:15",
        "updateTime": "2021-08-12 11:07:15",
        "collections": [],
        "skus": [
            {
                "skuId": "1425655086088429570",
                "spuId": "1425655086029709313",
                "barcode": "111",
                "shopId": 100103,
                "compareAtPrice": 10000,
                "price": 10000,
                "inventoryQuantity": 0,
                "skuNote": null,
                "sku": "11",
                "skuImage": null,
                "specOption1": null,
                "specOption2": null,
                "specOption3": null,
                "weight": 1.00,
                "weightUnit": "kg"
            }
        ],
        "images": [
            {
                "position": 0,
                "imageData": {
                    "fileRepositoryId": "1357301923878989826",
                    "path": "1357301923878989826.png",
                    "url": "https://cdn.shoptop.com/1357301923878989826.png",
                    "height": 239,
                    "width": 378,
                    "type": "image/png"
                }
            }
        ],
        "specs": [],
        "goodsTags": []
    }
}
```

#### 创建商品
**URL:** /openapi/v1/products/

**Type:** POST

**Content-Type:** application/json; charset=utf-8

**Description:** 创建商品

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
spuId|number|商品spuID|false|v1
seoTitle|string|seo标题|false|v1
seoKeywords|string|seo关键词|false|v1
seoDescription|string|seo描述|false|v1
handle|string|商品url尾缀|false|v1
goodsTitle|string|商品标题|false|v1
goodsBrief|string|副标题|false|v1
spu|string|spu属性|false|v1
inventoryTracking|number|是否跟踪库存,0:不跟踪,1:跟踪|false|v1
inventoryPolicy|number|跟踪库存策略,1:库存为0时不允许购买,2:库存为0时允许购买,3:库存为0时自动下架|false|v1
needVariantImage|number|sku款式是否需要配图,1:需要,0:不需要|false|v1
published|number|是否上架|false|v1
publishedAt|string|上架时间|false|v1
requiresShipping|number|是否需要物流|false|v1
taxable|number|是否对此商品收税|false|v1
vendorName|string|供应商名称|false|v1
vendorUrl|string|供应商url|false|v1
amazonLink|string|亚马逊商品链接|false|v1
shopId|number|店铺id|false|v1
goodsDescription|string|商品描述|false|v1
isFreeShipping|number|是否免运费(0不免运费,1免运费)|false|v1
isSensitiveGoods|number|是否为敏感商品|false|v1
skus|array|商品sku列表|false|v1
└─skuId|number|商品skuID|false|v1
└─spuId|number|店铺id|false|v1
└─barcode|string|条形码|false|v1
└─shopId|number|店铺id|false|v1
└─compareAtPrice|number|原价|false|v1
└─price|number|售价|false|v1
└─inventoryQuantity|number|库存|false|v1
└─skuNote|string|备注|false|v1
└─sku|string|sku属性|false|v1
└─skuImage|string|图片|false|v1
└─specOption1|string|规格属性1|false|v1
└─specOption2|string|规格属性2|false|v1
└─specOption3|string|规格属性3|false|v1
└─weight|number|重量|false|v1
└─weightUnit|string|重量单位|false|v1
imageUrls|array|商品图片列表(第一张是主图）|false|v1
specs|array|商品规格列表|false|v1
└─spuSpecName|string|规格名称|false|v1
└─position|number|规格顺序|false|v1
└─spuSpecValues|array|规格值|false|v1
goodsTags|array|商品标签|false|v1
isSingleSku|number|是否单一款式|false|v1
inventoryQuantity|number|库存数量|false|v1

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/products/ --data '{
	"spuId": 2b3d272d-12b5-4133-bbb0-5e43e748c730,
	"seoTitle": "7u405b",
	"seoKeywords": "72vhiz",
	"seoDescription": "vbf73k",
	"handle": "mt1aiu",
	"goodsTitle": "win3i8",
	"goodsBrief": "25dzw6",
	"spu": "phphza",
	"inventoryTracking": 321,
	"inventoryPolicy": 521,
	"needVariantImage": 29,
	"published": 839,
	"publishedAt": "2021-08-12 09:29:39",
	"requiresShipping": 196,
	"taxable": 509,
	"vendorName": "玛丽",
	"vendorUrl": "www.shoptop.com",
	"amazonLink": "ue4bb5",
	"shopId": 610,
	"goodsDescription": "z5u1j1",
	"isFreeShipping": 21,
	"isSensitiveGoods": 48,
	"skus": [
		{
			"skuId": 2b3d272d-12b5-4133-bbb0-5e43e748c730,
			"spuId": 2b3d272d-12b5-4133-bbb0-5e43e748c730,
			"barcode": "79665",
			"shopId": 531,
			"compareAtPrice": 187,
			"price": 169,
			"inventoryQuantity": 450,
			"skuNote": "lspe8w",
			"sku": "9cn7vk",
			"skuImage": "dw72cc",
			"specOption1": "64exvb",
			"specOption2": "3i0mo3",
			"specOption3": "41uw9d",
			"weight": 623,
			"weightUnit": "qinlyv"
		}
	],
	"imageUrls": [
		"6y32nw"
	],
	"specs": [
		{
			"spuSpecName": "健柏罗",
			"position": 172,
			"spuSpecValues": [
				"y0xt71"
			]
		}
	],
	"goodsTags": [
		"0y4boa"
	],
	"isSingleSku": 159,
	"inventoryQuantity": 472
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─status|string|状态|v1

**Response-example:**
```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status": "success"
	}
}
```

#### 删除商品
**URL:** /openapi/v1/products/{spuId}

**Type:** DELETE


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 删除商品

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
spuId|number|商品id|true|v1

**Request-example:**
```
curl -X DELETE -i /openapi/v1/products/2b3d272d-12b5-4133-bbb0-5e43e748c730
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─status|string|状态|v1

**Response-example:**
```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status": "success"
	}
}
```

#### 更新商品
**URL:** /openapi/v1/products/

**Type:** PUT


**Content-Type:** application/json; charset=utf-8

**Description:** 更新商品

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
spuId|number|商品spuID|false|v1
seoTitle|string|seo标题|false|v1
seoKeywords|string|seo关键词|false|v1
seoDescription|string|seo描述|false|v1
handle|string|商品url尾缀|false|v1
goodsImage|object|商品主图|false|v1
└─path|string|文件路径尾缀|false|v1
goodsTitle|string|商品标题|false|v1
goodsBrief|string|副标题|false|v1
spu|string|spu属性|false|v1
inventoryTracking|number|是否跟踪库存,0:不跟踪,1:跟踪|false|v1
inventoryPolicy|number|跟踪库存策略,1:库存为0时不允许购买,2:库存为0时允许购买,3:库存为0时自动下架|false|v1
needVariantImage|number|sku款式是否需要配图,1:需要,0:不需要|false|v1
needVariantNote|number|sku款式是否需要备注,0:不需要,1:需要|false|v1
published|number|是否上架|false|v1
publishedAt|string|上架时间|false|v1
requiresShipping|number|是否需要物流|false|v1
taxable|number|是否对此商品收税|false|v1
vendorName|string|供应商名称|false|v1
vendorUrl|string|供应商url|false|v1
amazonLink|string|亚马逊商品链接|false|v1
shopId|number|店铺id|false|v1
goodsDescription|string|商品描述|false|v1
isFreeShipping|number|是否免运费(0不免运费,1免运费)|false|v1
isSensitiveGoods|number|是否为敏感商品|false|v1
skus|array|商品sku列表|false|v1
└─skuId|number|商品skuID|false|v1
└─spuId|number|店铺id|false|v1
└─barcode|string|条形码|false|v1
└─shopId|number|店铺id|false|v1
└─compareAtPrice|number|原价|false|v1
└─price|number|售价|false|v1
└─inventoryQuantity|number|库存|false|v1
└─skuNote|string|备注|false|v1
└─sku|string|sku属性|false|v1
└─skuImage|string|图片|false|v1
└─specOption1|string|规格属性1|false|v1
└─specOption2|string|规格属性2|false|v1
└─specOption3|string|规格属性3|false|v1
└─weight|number|重量|false|v1
└─weightUnit|string|重量单位|false|v1
imageUrls|array|商品图片列表(第一张是主图）|false|v1
specs|array|商品规格列表|false|v1
└─spuSpecName|string|规格名称|false|v1
└─position|number|规格顺序|false|v1
└─spuSpecValues|array|规格值|false|v1
goodsTags|array|商品标签|false|v1
isSingleSku|number|是否单一款式|false|v1
inventoryQuantity|number|库存数量|false|v1

**Request-example:**
```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/products/ --data '{
	"spuId": 2b3d272d-12b5-4133-bbb0-5e43e748c730,
	"seoTitle": "ive0ua",
	"seoKeywords": "xkeh3j",
	"seoDescription": "7enwys",
	"handle": "ma23n3",
	"goodsImage": {
		"path": "lh2c9c"
	},
	"goodsTitle": "nmzihy",
	"goodsBrief": "oi16dx",
	"spu": "2u519z",
	"inventoryTracking": 679,
	"inventoryPolicy": 9,
	"needVariantImage": 29,
	"needVariantNote": 88,
	"published": 521,
	"publishedAt": "2021-08-12 09:29:39",
	"requiresShipping": 389,
	"taxable": 377,
	"vendorName": "健柏罗",
	"vendorUrl": "www.shoptop.com",
	"amazonLink": "9bgd8r",
	"shopId": 139,
	"goodsDescription": "zv6t9m",
	"isFreeShipping": 337,
	"isSensitiveGoods": 576,
	"skus": [
		{
			"skuId": 2b3d272d-12b5-4133-bbb0-5e43e748c730,
			"spuId": 2b3d272d-12b5-4133-bbb0-5e43e748c730,
			"barcode": "79665",
			"shopId": 417,
			"compareAtPrice": 826,
			"price": 861,
			"inventoryQuantity": 911,
			"skuNote": "5vy8pw",
			"sku": "a3n0x2",
			"skuImage": "d6njf1",
			"specOption1": "zb5e94",
			"specOption2": "4oe7fa",
			"specOption3": "cvy98q",
			"weight": 4,
			"weightUnit": "01zxa3"
		}
	],
	"imageUrls": [
		"https://dev-cdn-shoptop-com.oss-cn-shanghai.aliyuncs.com/file/png/1339860432684871681.png"
	],
	"specs": [
		{
			"spuSpecName": "健柏罗",
			"position": 460,
			"spuSpecValues": [
				"uri4ib"
			]
		}
	],
	"goodsTags": [
		"mqpng4"
	],
	"isSingleSku": 706,
	"inventoryQuantity": 40
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─status|string|状态|v1

**Response-example:**
```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status": "success"
	}
}
```

### 子商品

v2版本即将发布，敬请期待！

### 商品图片

v2版本即将发布，敬请期待！

## 专辑API

### 专辑

#### 专辑列表

**URL:** /openapi/v1/collections/

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 专辑列表

**Query-parameters:**

| Parameter | Type   | Description                            | Required | Since |
| --------- | ------ | -------------------------------------- | -------- | ----- |
| pageNo    | number  | 页码，从1开始                          | true     | v1    |
| pageSize  | number  | 每页显示条数 default: 10, maximum: 200 | true     | v1    |
| keyword   | string | 搜索关键词                             | false    | v1    |
| shopId    | number  | 店铺id                                 | false    | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/collections/?keyword=cou2q7&shopId=150&pageSize=10&pageNo=161
```

**Response-fields:**

| Field                                            | Type   | Description          | Since |
| ------------------------------------------------ | ------ | -------------------- | ----- |
| code                                             | number  | code                 | v1    |
| errorCode                                        | number  | errorCode            | v1    |
| msg                                              | string | message              | v1    |
| data                                             | array  | data                 | v1    |
| └─collectionId                                   | number  | 专辑ID               | v1    |
| └─shopId                                         | number  | 店铺id               | v1    |
| └─seoTitle                                       | string | seo标题              | v1    |
| └─seoKeywords                                    | string | seo关键词            | v1    |
| └─seoDescription                                 | string | seo描述              | v1    |
| └─handle                                         | string | 商品url尾缀          | v1    |
| └─collectionDescription                          | string | 描述                 | v1    |
| └─collectionTitle                                | string | 标题                 | v1    |
| └─image                                          | object | 专辑封面图片         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId | number  | 图片文件id           | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path             | string | 图片位置             | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url              | string | 图片url              | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height           | number  | 高度                 | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width            | number  | 宽度                 | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type             | string | 文件格式类型         | v1    |
| └─sortOrder                                      | string | 专辑中商品的排序规则 | v1    |
| └─createTime                                     | string | 创建时间             | v1    |
| └─updateTime                                     | string | 修改时间             | v1    |
| └─spuIds                                         | array  | 商品专辑中的商品id   | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": [
        {
            "collectionId": "1341305767802273794",
            "shopId": 48,
            "seoTitle": "苏格拉没有底",
            "seoKeywords": "",
            "seoDescription": "",
            "handle": "苏格拉没有底",
            "collectionDescription": "",
            "collectionTitle": "苏格拉没有底",
            "image": {
                "fileRepositoryId": "1339860432684871681",
                "path": "1339860432684871681.png",
                "url": "https://dev-cdn-shoptop-com.oss-cn-shanghai.aliyuncs.com/file/png/2020/12/18/1339860432684871681.png",
                "height": 48,
                "width": 48,
                "type": "image/png"
            },
            "sortOrder": "{\"sortBy\":\"sale_num\",\"sortDirection\":\"descend\"}",
            "createTime": "2020-12-22 16:53:10",
            "updateTime": "2021-03-18 10:57:30",
            "spuIds": null
        },
        {
            "collectionId": "1341304543304581121",
            "shopId": 48,
            "seoTitle": "想象之中",
            "seoKeywords": "",
            "seoDescription": "",
            "handle": "想象之中_f3qv",
            "collectionDescription": "",
            "collectionTitle": "想象之中",
            "image": null,
            "sortOrder": "{\"sortBy\":\"sale_num\",\"sortDirection\":\"descend\"}",
            "createTime": "2020-12-22 16:48:18",
            "updateTime": "2021-03-18 10:57:30",
            "spuIds": null
        }
    ]
}
```

#### 专辑数量

**URL:** /openapi/v1/collections/count

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 专辑数量

**Query-parameters:**

| Parameter | Type   | Description | Required | Since |
| --------- | ------ | ----------- | -------- | ----- |
| keyword   | string | 搜索关键词  | false    | v1    |
| shopId    | number  | 店铺id      | false    | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/collections/count?shopId=321&keyword=shfn0c
```

**Response-fields:**

| Field     | Type   | Description | Since |
| --------- | ------ | ----------- | ----- |
| code      | number  | code        | v1    |
| errorCode | number  | errorCode   | v1    |
| msg       | string | message     | v1    |
| data      | object | data        | v1    |
| └─count   | number  | 数量        | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "count": "27"
    }
}
```

#### 专辑详情

**URL:** /openapi/v1/collections/{collectionId}

**Type:** GET

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 专辑详情

**Path-parameters:**

| Parameter    | Type  | Description | Required | Since |
| ------------ | ----- | ----------- | -------- | ----- |
| collectionId | number | 专辑id      | true     | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/collections/896
```

**Response-fields:**

| Field                                            | Type   | Description                                                  | Since |
| ------------------------------------------------ | ------ | ------------------------------------------------------------ | ----- |
| code                                             | number  | code                                                         | v1    |
| errorCode                                        | number  | errorCode                                                    | v1    |
| msg                                              | string | message                                                      | v1    |
| data                                             | object | data                                                         | v1    |
| └─collectionId                                   | number  | 专辑ID                                                       | v1    |
| └─shopId                                         | number  | 店铺id                                                       | v1    |
| └─seoTitle                                       | string | seo标题                                                      | v1    |
| └─seoKeywords                                    | string | seo关键词                                                    | v1    |
| └─seoDescription                                 | string | seo描述                                                      | v1    |
| └─handle                                         | string | 商品url尾缀                                                  | v1    |
| └─collectionDescription                          | string | 描述                                                         | v1    |
| └─collectionTitle                                | string | 标题                                                         | v1    |
| └─image                                          | object | 专辑封面图片                                                 | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─fileRepositoryId | number  | 图片文件id                                                   | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─path             | string | 图片位置                                                     | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─url              | string | 图片url                                                      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─height           | number  | 高度                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─width            | number  | 宽度                                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─type             | string | 文件格式类型                                                 | v1    |
| └─sortOrder                                      | object | 专辑中商品的排序规则                                         | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sortBy           | string | 要根据哪个字段进行排序                                       | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─sortDirection    | string | 根据哪个字段排序进行升序或降序排序 升序:ascend ,降序: descend | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "collectionId": "1341305767802273794",
        "shopId": 48,
        "seoTitle": "自动专辑有封面",
        "seoKeywords": "",
        "seoDescription": "",
        "handle": "自动专辑有封面",
        "collectionDescription": "",
        "collectionTitle": "自动专辑有封面",
        "image": {
            "fileRepositoryId": "1339860432684871681",
            "path": "1339860432684871681.png",
            "url": "https://dev-cdn-shoptop-com.oss-cn-shanghai.aliyuncs.com/file/png/2020/12/18/1339860432684871681.png",
            "height": 48,
            "width": 48,
            "type": "image/png"
        },
        "sortOrder": {
            "sortBy": "sale_num",
            "sortDirection": "descend"
        }
    }
}
```

#### 创建专辑

**URL:** /openapi/v1/collections/

**Type:** POST


**Content-Type:** application/json; charset=utf-8

**Description:** 创建专辑

**Body-parameters:**

| Parameter             | Type   | Description                                                  | Required | Since |
| --------------------- | ------ | ------------------------------------------------------------ | -------- | ----- |
| collectionId          | number  | 专辑ID                                                       | false    | v1    |
| shopId                | number  | 店铺id                                                       | false    | v1    |
| seoTitle              | string | seo标题                                                      | false    | v1    |
| seoKeywords           | string | seo关键词                                                    | false    | v1    |
| seoDescription        | string | seo描述                                                      | false    | v1    |
| handle                | string | 商品url尾缀                                                  | false    | v1    |
| collectionDescription | string | 描述                                                         | false    | v1    |
| collectionTitle       | string | 标题                                                         | false    | v1    |
| imageUrl              | string | 专辑封面图片                                                 | false    | v1    |
| sortOrder             | object | 专辑中商品的排序规则                                         | false    | v1    |
| └─sortBy              | string | 要根据哪个字段进行排序                                       | false    | v1    |
| └─sortDirection       | string | 根据哪个字段排序进行升序或降序排序 升序:ascend ,降序: descend | false    | v1    |
| spuIds                | array  | 商品专辑中的商品id                                           | false    | v1    |

**Request-example:**

```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/collections/ --data '{
	"collectionId": 87,
	"shopId": 709,
	"seoTitle": "fowyo2",
	"seoKeywords": "1dnj76",
	"seoDescription": "lntyaz",
	"handle": "55i1yv",
	"collectionDescription": "lm93ip",
	"collectionTitle": "weqhe4",
	"imageUrl": "https://dev-cdn-shoptop-com.oss-cn-shanghai.aliyuncs.com/file/png/1339860432684871681.png",
	"sortOrder": {
		"sortBy": "9wovus",
		"sortDirection": "exjaq1"
	},
	"spuIds": [
		931
	]
}'
```

**Response-fields:**

| Field     | Type   | Description | Since |
| --------- | ------ | ----------- | ----- |
| code      | number  | code        | v1    |
| errorCode | number  | errorCode   | v1    |
| msg       | string | message     | v1    |
| data      | object | data        | v1    |
| └─status  | string | 状态        | v1    |

**Response-example:**

```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status": "success"
	}
}
```

#### 编辑专辑

**URL:** /openapi/v1/collections/

**Type:** PUT


**Content-Type:** application/json; charset=utf-8

**Description:** 编辑专辑

**Body-parameters:**

| Parameter             | Type   | Description                                                  | Required | Since |
| --------------------- | ------ | ------------------------------------------------------------ | -------- | ----- |
| collectionId          | number  | 专辑ID                                                       | false    | v1    |
| shopId                | number  | 店铺id                                                       | false    | v1    |
| seoTitle              | string | seo标题                                                      | false    | v1    |
| seoKeywords           | string | seo关键词                                                    | false    | v1    |
| seoDescription        | string | seo描述                                                      | false    | v1    |
| handle                | string | 商品url尾缀                                                  | false    | v1    |
| collectionDescription | string | 描述                                                         | false    | v1    |
| collectionTitle       | string | 标题                                                         | false    | v1    |
| imageUrl              | string | 专辑封面图片                                                 | false    | v1    |
| sortOrder             | object | 专辑中商品的排序规则                                         | false    | v1    |
| └─sortBy              | string | 要根据哪个字段进行排序                                       | false    | v1    |
| └─sortDirection       | string | 根据哪个字段排序进行升序或降序排序 升序:ascend ,降序: descend | false    | v1    |
| spuIds                | array  | 商品专辑中的商品id                                           | false    | v1    |

**Request-example:**

```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/collections/ --data '{
	"collectionId": 31,
	"shopId": 920,
	"seoTitle": "6ynhnr",
	"seoKeywords": "uzsw8k",
	"seoDescription": "lvkw9j",
	"handle": "2kbuak",
	"collectionDescription": "ym0bgq",
	"collectionTitle": "ljsah0",
	"imageUrl": "https://dev-cdn-shoptop-com.oss-cn-shanghai.aliyuncs.com/file/png/1339860432684871681.png",
	"sortOrder": {
		"sortBy": "07hgep",
		"sortDirection": "rx98tz"
	},
	"spuIds": [
		91
	]
}'
```

**Response-fields:**

| Field     | Type   | Description | Since |
| --------- | ------ | ----------- | ----- |
| code      | number  | code        | v1    |
| errorCode | number  | errorCode   | v1    |
| msg       | string | message     | v1    |
| data      | object | data        | v1    |
| └─status  | string | 状态        | v1    |

**Response-example:**

```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status": "success"
	}
}
```

#### 删除专辑

**URL:** /openapi/v1/collections/{collectionId}

**Type:** DELETE


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 删除专辑

**Path-parameters:**

| Parameter    | Type  | Description | Required | Since |
| ------------ | ----- | ----------- | -------- | ----- |
| collectionId | number | 专辑id      | true     | v1    |

**Request-example:**

```
curl -X DELETE -i /openapi/v1/collections/972
```

**Response-fields:**

| Field     | Type   | Description | Since |
| --------- | ------ | ----------- | ----- |
| code      | number  | code        | v1    |
| errorCode | number  | errorCode   | v1    |
| msg       | string | message     | v1    |
| data      | object | data        | v1    |
| └─status  | string | 状态        | v1    |

**Response-example:**

```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status": "success"
	}
}
```

### 专辑关联

#### 专辑关联列表

**URL:** /openapi/v1/collects/

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 专辑关联列表

**Query-parameters:**

| Parameter    | Type  | Description                            | Required | Since |
| ------------ | ----- | -------------------------------------- | -------- | ----- |
| pageNo       | number | 页码，从1开始                          | true     | v1    |
| pageSize     | number | 每页显示条数 default: 10, maximum: 200 | true     | v1    |
| shopId       | number | 店铺id                                 | false    | v1    |
| spuId        | number | 商品id                                 | false    | v1    |
| collectionId | number | 专辑id                                 | false    | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/collects/?shopId=807&spuId=2b3d272d-12b5-4133-bbb0-5e43e748c730&pageSize=10&collectionId=553&pageNo=571
```

**Response-fields:**

| Field                                        | Type   | Description | Since |
| -------------------------------------------- | ------ | ----------- | ----- |
| code                                         | number  | code        | v1    |
| errorCode                                    | number  | errorCode   | v1    |
| msg                                          | string | message     | v1    |
| data                                         | object | data        | v1    |
| └─list                                       | array  | 数据        | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id           | number  | 关联id      | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─collectionId | number  | 商品专辑id  | v1    |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─spuId        | number  | 商品spuID   | v1    |
| └─total                                      | number  | 总量        | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "list": [
            {
                "id": "1298904901201625089",
                "collectionId": "1298549559691182082",
                "spuId": "1298896645695639553"
            },
            {
                "id": "1298904901226790913",
                "collectionId": "1298549559691182082",
                "spuId": "1298548593864605698"
            }        
        ],
        "total": "1298"
    }
}
```

#### 专辑关联数量

**URL:** /openapi/v1/collects/count

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 专辑关联数量

**Query-parameters:**

| Parameter    | Type  | Description | Required | Since |
| ------------ | ----- | ----------- | -------- | ----- |
| shopId       | number | 店铺id      | false    | v1    |
| spuId        | number | 商品id      | false    | v1    |
| collectionId | number | 专辑id      | false    | v1    |

**Request-example:**

```
curl -X GET -i /openapi/v1/collects/count?collectionId=83&shopId=767&spuId=2b3d272d-12b5-4133-bbb0-5e43e748c730
```

**Response-fields:**

| Field     | Type   | Description | Since |
| --------- | ------ | ----------- | ----- |
| code      | number  | code        | v1    |
| errorCode | number  | errorCode   | v1    |
| msg       | string | message     | v1    |
| data      | object | data        | v1    |
| └─count   | number  | 数量        | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "count": "1298"
    }
}
```



## Webhook

您可以使用webhook订阅来接收商店中特定事件的通知。订阅Webhook后，可以在安装了应用的商店中发生特定事件后立即让应用执行相关代码，而不必定期进行API调用以检查其状态。例如，当客户创建购物车或商户在创建新产品时，您可以依靠webhook触发应用中的相关操作。通过使用webhooks订阅，可以减少API调用，从而确保您的应用更高效，更新速度更快。

webhook通知会包含HTTP headers和JSON格式数据。以`orders/create`事件为例，会包含如下headers：

- **X-Shoptop-Topic**: `orders/create`
- **X-Shoptop-Hmac-Sha256**: `XWmrwMey6OsLMeiZKwP4FppHH3cmAiiJJAweH5Jo4bM=`
- **X-Shoptop-Shop-Domain**: `abc.ishoptop.com`
- **X-Shoptop-Api-Version**: `v1`

其中，`X-Shoptop-Hmac-Sha256`可以用来验证webhooks，`X-Shoptop-Shop-Domain`则标识了此事件对应的店铺。`X-Shoptop-Api-Version`表明了使用的webhook API版本。

### 支持的webhook事件和主题

| Events             | Topics                                                       | Response Example                                             |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Collection         | collections/create, <br>collections/update                   | {<br/>      "collectionId": "1405475539787186178",<br/>      "shopId": 100169,<br/>      "seoTitle": "专辑1",<br/>      "seoKeywords": null,<br/>      "seoDescription": null,<br/>      "handle": "专辑1",<br/>      "collectionDescription": null,<br/>      "collectionTitle": "专辑1-2021-06-17 18:40:55",<br/>      "image": null,<br/>      "sortOrder": "{\"sortBy\":\"sale_num\",\"sortDirection\":\"descend\"}",<br/>      "createTime": "2021-06-17 18:40:56",<br/>      "updateTime": "2021-06-17 18:40:56",<br/>      "spuIds": null<br/>} |
| collections/delete | {<br/>	"collection": {<br/>		"id": "0097cfc8-6ec3-40ed-ab9f-6f2231b705a6"<br/>	}<br/>} |                                                              |
| Product            | products/create, <br>products/update                         | {<br/>        "spuId": "1405475512834588674",<br/>        "seoTitle": "Negative-ion Cat Ear Cute Humidifier",<br/>        "seoKeywords": null,<br/>        "seoDescription": null,<br/>        "handle": "negative-ion-moisturizing-humidifier",<br/>        "goodsImage": {<br/>            "fileRepositoryId": "1405475512637456385",<br/>            "path": "1405475512637456385.jpg",<br/>            "url": "https://dev-cdn-shoptop-com/1405475512637456385.jpg",<br/>            "height": 1000,<br/>            "width": 1000,<br/>            "type": "image/jpg"<br/>        },<br/>        "goodsTitle": "Negative-ion Cat Ear Cute Humidifier",<br/>        "goodsBrief": null,<br/>        "spu": null,<br/>        "inventoryTracking": 1,<br/>        "inventoryPolicy": 2,<br/>        "needVariantImage": 1,<br/>        "published": 1,<br/>        "publishedAt": null,<br/>        "requiresShipping": 1,<br/>        "taxable": 0,<br/>        "vendorName": "CJ",<br/>        "vendorUrl": null,<br/>        "amazonLink": null,<br/>        "shopId": 100169,<br/>        "goodsDescription": "",<br/>        "isFreeShipping": 0,<br/>        "isSensitiveGoods": 0,<br/>        "isSingleSku": 0,<br/>        "inventoryQuantity": 20,<br/>        "createTime": "2021-06-17 18:40:49",<br/>        "updateTime": "2021-06-17 18:41:22",<br/>        "collections": [],<br/>        "skus": ”“<br/>        "images":”“<br/>        "goodsTags": []<br/>    } |
| products/delete    | {<br/>	"product": {<br/>		"id": "0097cfc8-6ec3-40ed-ab9f-6f2231b705a6"<br/>	}<br/>} |                                                              |
| Order              | orders/cancelled, <br>orders/create, <br>orders/fulfilled, <br>orders/finished, <br>orders/paid, <br>orders/partially_fulfilled, <br>orders/refunded, <br>orders/update | {<br/>	"orderNo": "ZLN0398018",<br/>	"id": "1424682429884698626",<br/>	"totalPrice": "5.20",<br/>	"subTotal": "5.00",<br/>	"currency": "USD",<br/>	"financialStatus": "paid",<br/>	"orderStatus": "finished",<br/>	"canceledAt": null,<br/>	"cancelReason": null,<br/>	"orderNote": null,<br/>	"fulfillmentStatus": "finished",<br/>	"customerDeletedAt": null,<br/>	"placedAt": "2021-08-09 18:42:31",<br/>	"tags": null,<br/>	"discountCode": null,<br/>	"codeDiscountTotal": null,<br/>	"lineItemDiscountTotal": null,<br/>	"customerNote": "",<br/>	"totalDiscount": null,<br/>	"totalTax": "0.00",<br/>	"totalShipping": "0.20",<br/>	"createdAt": "2021-08-09 18:42:15",<br/>	"updatedAt": "2021-08-12 14:26:26",<br/>	"lineItems": "",<br/>	"paymentLine": {<br/>		"paymentChannel": "cod",<br/>		"paymentMethod": "cod",<br/>		"transactionNo": null,<br/>		"merchantId": null,<br/>		"merchantEmail": null<br/>	},<br/>	"shippingLine": {<br/>		"name": "亚洲"<br/>	},<br/>	"billingAddress": null,<br/>	"shippingAddress": "",<br/>	"fulfillments": "",<br/>	"customer": {<br/>		"email": "2943089171@qq.com",<br/>		"firstName": "22",<br/>		"lastName": "11",<br/>		"ordersCount": "0",<br/>		"totalSpent": "0.00",<br/>		"phone": null,<br/>		"createdAt": "2021-08-09 18:42:31",<br/>		"updatedAt": null<br/>	}<br/>} |
| orders/delete      | {<br/>	"order": {<br/>		"id": "0097cfc8-6ec3-40ed-ab9f-6f2231b705a6"<br/>	}<br/>} |                                                              |
| Fulfillment        | fulfillments/create, <br>fulfillments/update                 | {<br/>        "id": "1424684956927692801",<br/>        "orderId": "1424682429884698626",<br/>        "status": "finished",<br/>        "createdAt": "2021-08-09 18:52:18",<br/>        "updatedAt": "2021-08-12 14:26:26",<br/>        "trackingCompany": "安得物流",<br/>        "trackingNumber": "21421",<br/>        "trackingCompanyCode": "annto",<br/>        "lineItems": [<br/>            {<br/>                "productTitle": "水杯",<br/>                "variantTitle": "水杯",<br/>                "quantity": 1,<br/>                "note": null,<br/>                "image": "https://cdn.shoptop.com/1405475536159113218.jpg",<br/>                "price": "5.00",<br/>                "compareAtPrice": "10.00",<br/>                "total": "5.00",<br/>                "sku": "201201",<br/>                "weight": "500.00",<br/>                "weightUnit": "g",<br/>                "vendor": null,<br/>                "properties": null,<br/>                "productUrl": null,<br/>                "productHandle": "水杯",<br/>                "id": "1424682432267063297",<br/>                "productId": "1405475536335273985",<br/>                "variantId": "1405475536360439811",<br/>                "fulfillmentStatus": "finished"<br/>            }<br/>        ]<br/>    } |
| Customer           | customers/create, <br>customers/update                       | {<br/>	"customer": {<br/>		"id": "1dfa86e9-f3f4-4684-8eb6-05399ef19705",<br/>		"first_name": "Lee",<br/>		"last_name": "Le",<br/>		"name": "Lee Le",<br/>		"email": "person207@example.com",<br/>		"phone": "15323233434",<br/>		"phone_area_code": "+86",<br/>		"accepts_marketing": false,<br/>		"orders_count": 10,<br/>		"total_spent": "100.10",<br/>		"tags": "",<br/>		"created_at": "2018-10-23T15:29:24-04:00",<br/>		"updated_at": "2018-10-23T15:29:24-04:00",<br/>		"default_address": {<br/>			"id": "2ffa86e9-f3f4-4684-84b6-05399ef19709",<br/>			"customer_id": "1dfa86e9-f3f4-4684-8eb6-05399ef19705",<br/>			"first_name": null,<br/>			"last_name": null,<br/>			"company": null,<br/>			"address1": "Chestnut Street 92",<br/>			"address2": "",<br/>			"city": "Louisville",<br/>			"province": "Kentucky",<br/>			"country": "United States",<br/>			"zip": "40202",<br/>			"phone": "555-625-1199",<br/>			"phone_area_code": "+86",<br/>			"name": "",<br/>			"province_code": "KY",<br/>			"country_code": "US",<br/>			"country_name": "United States",<br/>			"default": true<br/>		},<br/>		"addresses": [{<br/>			"id": "2ffa86e9-f3f4-4684-84b6-05399ef19709",<br/>			"customer_id": "1dfa86e9-f3f4-4684-8eb6-05399ef19705",<br/>			"first_name": null,<br/>			"last_name": null,<br/>			"company": null,<br/>			"address1": "Chestnut Street 92",<br/>			"address2": "",<br/>			"city": "Louisville",<br/>			"province": "Kentucky",<br/>			"country": "United States",<br/>			"zip": "40202",<br/>			"phone": "555-625-1199",<br/>			"phone_area_code": "+86",<br/>			"name": "",<br/>			"province_code": "KY",<br/>			"country_code": "US",<br/>			"country_name": "United States",<br/>			"default": true<br/>		}]<br/>	}<br/>}<br/>` |
| customers/delete   | {<br/>	"customer": {<br/>		"id": "0097cfc8-6ec3-40ed-ab9f-6f2231b705a6"<br/>	}<br/>} |                                                              |
| Store              | app/uninstalled , <br>app/expired                            | {<br/>	"app": {<br/>		"id": 1,<br/>		"name": "enquiry",<br/>		"uid": "yAQSUe8l3hT8YFHNAZBIofELvLE0s",<br/>		"redirect_uri": "https://enquiry.apps.shoptop.com/callback/shoptop/oauth",<br/>		"scopes": [],<br/>		"icon": "/oss/operation/9ea4e43d0be77b367d3a6f403d10b6e3.svg",<br/>		"webhook_api_version": "2020-01",<br/>		"created_at": "2020-01-15T02:33:27Z",<br/>		"updated_at": "2020-01-15T02:33:27Z"<br/>	}<br/>} |


### Webhook API

#### webhook列表

**URL:** /openapi/v1/webhooks/

**Type:** GET

**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** webhook列表

**Query-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
pageNo|number|页码，从1开始|true|v1
pageSize|number|每页显示条数 default: 10, maximum: 200|true|v1
id|number|主键ID|false|v1
address|string|webhook通知地址|false|v1
topic|string|订阅事件名称|false|v1
createTimeBegin|string|创建时间Begin|false|v1
createTimeEnd|string|创建时间End|false|v1
updateTimeBegin|string|更新时间Begin|false|v1
updateTimeEnd|string|更新时间End|false|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/webhooks/?updateTimeEnd=2021-08-12 09:29:39&updateTimeBegin=2021-08-12 09:29:39&pageNo=903&pageSize=10&address=http://facebook.ishoptop.com&createTimeBegin=2021-08-12 09:29:39&topic=34kwex&createTimeEnd=2021-08-12 09:29:39&id=87
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─list|array|数据|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─id|number|主键ID|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─address|string|webhook通知地址|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─topic|string|订阅事件名称|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─createTime|string|订阅时间|v1
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;└─updateTime|string|更新时间|v1
└─total|number|总量|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "list": [
            {
                "id": "1425707051551563777",
                "address": "http://localhost:8080/webhooks",
                "topic": "orders/create",
                "createTime": "2021-08-12 14:33:44",
                "updateTime": null
            }
        ],
        "total": "1"
    }
}
```

#### webhook数量
**URL:** /openapi/v1/webhooks/count

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** webhook数量

**Request-example:**
```
curl -X GET -i /openapi/v1/webhooks/count
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─count|number|数量|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "count": "1"
    }
}
```

#### webhook详情
**URL:** /openapi/v1/webhooks/{id}

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** webhook详情

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|id|true|v1

**Request-example:**
```
curl -X GET -i /openapi/v1/webhooks/132
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─id|number|主键ID|v1
└─address|string|webhook通知地址|v1
└─topic|string|订阅事件名称|v1
└─createTime|string|订阅时间|v1
└─updateTime|string|更新时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425707051551563777",
        "address": "http://localhost:8080/webhooks",
        "topic": "orders/create",
        "createTime": "2021-08-12 14:33:44",
        "updateTime": null
    }
}
```

#### 创建webhook
**URL:** /openapi/v1/webhooks/

**Type:** POST


**Content-Type:** application/json; charset=utf-8

**Description:** 创建webhook

**Request-headers:**

Header | Type|Description|Required|Since
---|---|---|---|----
Access-Token|string|访问token|true|v1


**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
address|string|webhook通知地址|false|v1
topic|string|订阅的事件主题|false|v1

**Request-example:**
```
curl -X POST -H 'Content-Type: application/json; charset=utf-8' -H 'Access-Token' -i /openapi/v1/webhooks/ --data '{
	"address":"http://localhost:8080/webhooks",
  	"topic":"orders/create"
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─id|number|主键ID|v1
└─address|string|webhook通知地址|v1
└─topic|string|订阅事件名称|v1
└─createTime|string|订阅时间|v1
└─updateTime|string|更新时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425707051551563777",
        "address": "http://localhost:8080/webhooks",
        "topic": "orders/create",
        "createTime": "2021-08-12 14:33:44",
        "updateTime": null
    }
}
```

更新webhook

**URL:** /openapi/v1/webhooks/

**Type:** PUT


**Content-Type:** application/json; charset=utf-8

**Description:** 更新webhook

**Body-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|主键ID|false|v1
address|string|webhook通知地址|false|v1
topic|string|订阅的事件主题|false|v1

**Request-example:**
```
curl -X PUT -H 'Content-Type: application/json; charset=utf-8' -i /openapi/v1/webhooks/ --data '{
	"id": 667,
	"address":"http://localhost:8080/webhooks",
  	"topic":"orders/create"
}'
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1
└─id|number|主键ID|v1
└─address|string|webhook通知地址|v1
└─topic|string|订阅事件名称|v1
└─createTime|string|订阅时间|v1
└─updateTime|string|更新时间|v1

**Response-example:**
```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "id": "1425707051551563777",
        "address": "http://localhost:8080/webhooks",
        "topic": "orders/create",
        "createTime": "2021-08-12 14:33:44",
        "updateTime": null
    }
}
```

#### 删除webhook
**URL:** /openapi/v1/webhooks/{id}

**Type:** DELETE


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 删除webhook

**Path-parameters:**

Parameter | Type|Description|Required|Since
---|---|---|---|---
id|number|id|true|v1

**Request-example:**
```
curl -X DELETE -i /openapi/v1/webhooks/749
```
**Response-fields:**

Field | Type|Description|Since
---|---|---|---
code|number|code|v1
errorCode|number|errorCode|v1
msg|string|message|v1
data|object|data|v1

**Response-example:**
```json
{
	"code": 0,
	"errorCode": 0,
	"msg": "请求成功",
	"data": {
		"status":"success"
	}
}
```

## 店铺API

### 店铺信息

**URL:** /openapi/v1/shop/

**Type:** GET


**Content-Type:** application/x-www-form-urlencoded;charset=utf-8

**Description:** 店铺信息

**Request-example:**

```
curl -X GET -i /openapi/v1/shop/
```

**Response-fields:**

| Field                | Type   | Description                    | Since |
| -------------------- | ------ | ------------------------------ | ----- |
| code                 | number  | code                           | v1    |
| errorCode            | number  | errorCode                      | v1    |
| msg                  | string | message                        | v1    |
| data                 | object | data                           | v1    |
| └─shopId             | number  | 店铺id                         | v1    |
| └─shopName           | string | 店铺名称                       | v1    |
| └─shopUrl            | string | 店铺URL                        | v1    |
| └─shopFavicon        | string | 店铺favicon                    | v1    |
| └─shopEmail          | string | 店主邮箱                       | v1    |
| └─shopServiceEmail   | string | 服务邮箱(客服)                 | v1    |
| └─shopFinancialEmail | string | 财务邮箱                       | v1    |
| └─shopContactEmail   | string | 联系邮箱                       | v1    |
| └─shopCurrency       | string | 币种                           | v1    |
| └─shopUtc            | string | 时区                           | v1    |
| └─shopLanguage       | string | 语言                           | v1    |
| └─utcHour            | double | 小时数                         | v1    |
| └─countryCode        | string | 国家代码                       | v1    |
| └─provinceCode       | string | 省份代码                       | v1    |
| └─city               | string | 城市                           | v1    |
| └─address            | string | 地址                           | v1    |
| └─phone              | string | 电话                           | v1    |
| └─zip                | string | 邮编                           | v1    |
| └─moneyFormat        | string | 货币格式                       | v1    |
| └─orderPrefix        | string | 订单前缀                       | v1    |
| └─symbol             | string | 货币符号                       | v1    |
| └─symbolLeft         | string | 货币符号左                     | v1    |
| └─symbolRight        | string | 货币符号右                     | v1    |
| └─beginTime          | string | 开始日期                       | v1    |
| └─endTime            | string | 结束日期                       | v1    |
| └─createTime         | string | 创建时间                       | v1    |
| └─updateTime         | string | 更新時間                       | v1    |
| └─symbolStatus       | number  | 币种状态,0:可修改,1:不可以修改 | v1    |

**Response-example:**

```json
{
    "code": 0,
    "errorCode": 0,
    "msg": "请求成功",
    "data": {
        "shopId": 48,
        "shopName": "Johnny_Store",
        "shopUrl": "johnny110.testgoshoptop.com",
        "shopFavicon": "https://mall-hk.oss-cn-hongkong.aliyuncs.com/file/jpeg/2020/12/10/1336990579871547394.jpeg",
        "shopEmail": "779482518@qq.com",
        "shopServiceEmail": "779482518@qq.com",
        "shopFinancialEmail": "779482518@qq.com",
        "shopContactEmail": null,
        "shopCurrency": "CAD",
        "shopUtc": "+0800",
        "shopLanguage": "en_US",
        "utcHour": 0.0,
        "countryCode": null,
        "provinceCode": null,
        "city": null,
        "address": null,
        "phone": null,
        "zip": null,
        "moneyFormat": null,
        "orderPrefix": null,
        "symbol": "Can.＄",
        "symbolLeft": "Can.＄",
        "symbolRight": "",
        "beginTime": null,
        "endTime": null,
        "createTime": "2020-12-04 10:12:42",
        "updateTime": "2021-03-15 16:20:17",
        "symbolStatus": 1
    }
}
```



## 数据字典
### http状态码字典

Code |Type|Description
---|---|---
0 |string|success
1 |string|failed
200|string|ok
400|string|Bad Request
401|string|Unauthorized
403|string|Forbidden
404|string|Not Found
415|string|Unsupported Media Type
429|string|Too many requests
500|string|Internal Server Error
502|string|Bad Gateway
503|string|Service Unavailable
