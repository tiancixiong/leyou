package com.leyou.client;

import com.leyou.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 商品分类的FeignClient
 */
@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {
}