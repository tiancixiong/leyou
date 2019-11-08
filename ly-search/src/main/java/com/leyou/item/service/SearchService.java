package com.leyou.item.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.client.CategoryClient;
import com.leyou.item.client.GoodsClient;
import com.leyou.item.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-07 22:20
 */
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * 将Spu转变为Goods
     *
     * @param spu
     * @return
     * @throws IOException
     */
    public Goods buildGoods(Spu spu) throws IOException {
        // 创建goods对象
        Goods goods = new Goods();

        // 查询商品分类名称
        List<String> names = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        // 查询sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        // 查询详情
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        // 查询规格参数
        List<SpecParam> params = this.specificationClient.querySpecParam(null, spu.getCid3(), true, null);

        // 处理sku，仅封装id、价格、标题、图片，并获得价格集合
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(skuMap);
        });

        // 处理规格参数
        Map<String, Object> genericSpecs = mapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> specialSpecs = mapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, Object>>() {
        });
        // 获取可搜索的规格参数
        Map<String, Object> searchSpec = new HashMap<>();

        // 过滤规格模板，把所有可搜索的信息保存到Map中
        Map<String, Object> specMap = new HashMap<>();
        params.forEach(p -> {
            if (p.getSearching()) {
                if (p.getGeneric()) {
                    String value = genericSpecs.get(p.getId().toString()).toString();
                    if (p.getNumeric()) {
                        value = chooseSegment(value, p);
                    }
                    specMap.put(p.getName(), StringUtils.isBlank(value) ? "其它" : value);
                } else {
                    specMap.put(p.getName(), specialSpecs.get(p.getId().toString()));
                }
            }
        });

        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(mapper.writeValueAsString(skuList));
        goods.setSpecs(specMap);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }
}
