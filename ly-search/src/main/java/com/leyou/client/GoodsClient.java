package com.leyou.client;

import com.leyou.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 商品的FeignClient
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
