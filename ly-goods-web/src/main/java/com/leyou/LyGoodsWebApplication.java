package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-11 13:33
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LyGoodsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyGoodsWebApplication.class, args);
    }
}
