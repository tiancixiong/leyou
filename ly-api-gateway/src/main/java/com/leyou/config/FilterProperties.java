package com.leyou.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description: 配置Filter白名单
 * @Date: Created in 2019-11-14 12:54
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "leyou.filter")
public class FilterProperties {
    private List<String> allowPaths;
}
