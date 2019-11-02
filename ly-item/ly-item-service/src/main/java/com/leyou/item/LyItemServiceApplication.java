package com.leyou.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-01 10:27
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper") // mapper接口的包扫描
public class LyItemServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyItemServiceApplication.class, args);
    }
}
