package com.leyou.item.bo;

import com.leyou.item.pojo.Spu;
import lombok.Data;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-05 8:24
 */
@Data
public class SpuBo extends Spu {
    String cname;// 商品分类名称
    String bname;// 品牌名称
}
