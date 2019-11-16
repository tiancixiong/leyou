package com.leyou.cart.pojo;

import lombok.Data;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-14 15:47
 */
@Data
public class Cart {
    private Long userId;// 用户id
    private Long skuId;// 商品id
    private String title;// 标题
    private String image;// 图片
    private Long price;// 加入购物车时的价格
    private Integer num;// 购买数量
    private String ownSpec;// 商品规格参数
}
