package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-01 10:15
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class LyApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyApiGatewayApplication.class, args);
    }
}
