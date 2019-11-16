package com.leyou.order.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-15 9:52
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "leyou.worker")
public class IdWorkerProperties {
    private long workerId;// 当前机器id

    private long datacenterId;// 序列号
}
