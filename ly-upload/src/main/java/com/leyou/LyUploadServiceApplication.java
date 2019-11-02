package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-02 16:18
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LyUploadServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyUploadServiceApplication.class, args);
    }
}
