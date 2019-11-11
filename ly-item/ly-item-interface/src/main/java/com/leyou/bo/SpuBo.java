package com.leyou.bo;

import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-05 8:24
 */
@Data
public class SpuBo extends Spu {
    @Transient
    String cname;// 商品分类名称
    @Transient
    String bname;// 品牌名称
    @Transient
    SpuDetail spuDetail;// 商品详情
    @Transient
    List<Sku> skus;// sku列表
}
