package com.leyou.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 提供商品分类服务接口
 */
@RequestMapping("/category")
public interface CategoryApi {

    @GetMapping("/names")
    List<String> queryNameByIds(@RequestParam("ids") List<Long> ids);
}