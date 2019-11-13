package com.leyou.sms.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: TianCi.Xiong
 * @Description: 阿里大鱼属性类
 * @Date: Created in 2019-11-13 16:10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "leyou.sms")
public class SmsProperties {
    // accessKeyId
    public String accessKeyId;
    // AccessKeySecret
    public String accessKeySecret;
    // 签名名称
    public String signName;
    // 模板名称
    public String verifyCodeTemplate;
}
