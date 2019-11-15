package com.leyou.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: TianCi.Xiong
 * @Description: 支付有关配置
 * @Date: Created in 2019-11-15 9:54
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "leyou.pay")
public class PayProperties {
    private String appId; // 公众账号ID

    private String mchId; // 商户号

    private String key; // 生成签名的密钥

    private int connectTimeoutMs; // 连接超时时间

    private int readTimeoutMs;// 读取超时时间
}
