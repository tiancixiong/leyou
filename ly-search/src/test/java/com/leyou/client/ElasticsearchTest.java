package com.leyou.client;

import com.leyou.LySearchApplication;
import com.leyou.item.bo.SpuBo;
import com.leyou.common.pojo.PageResult;
import com.leyou.service.SearchService;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: TianCi.Xiong
 * @Description:
 * @Date: Created in 2019-11-07 22:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class ElasticsearchTest {
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void createIndex() {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
    }

    /**
     * 把SPU变为Goods，然后写入索引库
     */
    @Test
    public void loadData() {
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            // 查询分页数据
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(page, rows, null, true);
            List<SpuBo> spus = result.getItems();
            size = spus.size();
            // 创建Goods集合
            List<Goods> goodsList = new ArrayList<>();
            // 遍历spu
            for (SpuBo spu : spus) {
                try {
                    Goods goods = this.searchService.buildGoods(spu);
                    goodsList.add(goods);
                } catch (Exception e) {
                    break;
                }
            }

            this.goodsRepository.saveAll(goodsList);
            page++;
        } while (size == 100);
    }
}
